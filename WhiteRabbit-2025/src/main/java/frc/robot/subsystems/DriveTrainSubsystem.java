// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.drive.DriveIO;
import frc.robot.subsystems.drive.DriveIO.DriveIOInputs;

/**
 * Drivetrain subsystem using hardware abstraction layer.
 * This subsystem works with both real hardware and simulation through the DriveIO interface.
 */
public class DriveTrainSubsystem extends SubsystemBase {
  private final DriveIO m_io;
  private final DriveIOInputs m_inputs = new DriveIOInputs();

  // Odometry for tracking robot pose (position and rotation) on the field
  private final DifferentialDriveOdometry m_odometry;

  // Field visualization for simulation/debugging
  private final Field2d m_field = new Field2d();

  /**
   * Creates a new DriveTrainSubsystem.
   * @param io The hardware abstraction layer implementation (real or sim)
   */
  public DriveTrainSubsystem(DriveIO io) {
    m_io = io;

    // Initialize inputs to get initial sensor readings
    m_io.updateInputs(m_inputs);

    // Initialize odometry with starting position and gyro angle
    m_odometry = new DifferentialDriveOdometry(
        m_inputs.gyroAngle,           // Initial gyro angle
        m_inputs.leftPositionMeters,  // Initial left encoder position
        m_inputs.rightPositionMeters, // Initial right encoder position
        new Pose2d()                  // Starting pose (0, 0, 0 degrees)
    );

    // Add field to SmartDashboard for visualization
    SmartDashboard.putData("Field", m_field);
  }

  public Command drive(Supplier<Double> leftSpeed, Supplier<Double> rightSpeed) {
    return new RunCommand(() -> {
      m_io.setTankDrive(-leftSpeed.get(), -rightSpeed.get());
    }, this);
  }

  /**
   * Stops all motors.
   */
  public void stop() {
    m_io.stop();
  }

  /**
   * Gets the left encoder position in meters.
   */
  public double getLeftPositionMeters() {
    return m_inputs.leftPositionMeters;
  }

  /**
   * Gets the right encoder position in meters.
   */
  public double getRightPositionMeters() {
    return m_inputs.rightPositionMeters;
  }

  /**
   * Gets the average distance traveled in meters.
   */
  public double getAverageDistanceMeters() {
    return (m_inputs.leftPositionMeters + m_inputs.rightPositionMeters) / 2.0;
  }

  /**
   * Gets the robot's heading from the gyroscope.
   */
  public double getHeadingDegrees() {
    return m_inputs.gyroAngle.getDegrees();
  }

  /**
   * Gets the robot's current pose (position and rotation) from odometry.
   */
  public Pose2d getPose() {
    return m_odometry.getPoseMeters();
  }

  /**
   * Resets the odometry to a specific pose.
   * Useful for setting the starting position in autonomous.
   * @param pose The pose to reset to
   */
  public void resetOdometry(Pose2d pose) {
    m_io.updateInputs(m_inputs);
    m_odometry.resetPosition(
        m_inputs.gyroAngle,
        m_inputs.leftPositionMeters,
        m_inputs.rightPositionMeters,
        pose
    );
  }

  @Override
  public void periodic() {
    // Update inputs from hardware/simulation
    m_io.updateInputs(m_inputs);

    // Update odometry with current sensor readings
    // This calculates the robot's 2D position based on wheel movements and heading
    m_odometry.update(
        m_inputs.gyroAngle,
        m_inputs.leftPositionMeters,
        m_inputs.rightPositionMeters
    );

    // Get the current pose from odometry
    Pose2d pose = m_odometry.getPoseMeters();

    // Update field visualization with correct 2D pose
    m_field.setRobotPose(pose);

    // Log telemetry to SmartDashboard
    SmartDashboard.putNumber("Drive/Left Position (m)", m_inputs.leftPositionMeters);
    SmartDashboard.putNumber("Drive/Right Position (m)", m_inputs.rightPositionMeters);
    SmartDashboard.putNumber("Drive/Left Velocity (m/s)", m_inputs.leftVelocityMetersPerSec);
    SmartDashboard.putNumber("Drive/Right Velocity (m/s)", m_inputs.rightVelocityMetersPerSec);
    SmartDashboard.putNumber("Drive/Heading (deg)", m_inputs.gyroAngle.getDegrees());
    SmartDashboard.putNumber("Drive/Left Voltage", m_inputs.leftAppliedVolts);
    SmartDashboard.putNumber("Drive/Right Voltage", m_inputs.rightAppliedVolts);
    SmartDashboard.putNumber("Drive/Left Current (A)", m_inputs.leftCurrentAmps);
    SmartDashboard.putNumber("Drive/Right Current (A)", m_inputs.rightCurrentAmps);

    // Log odometry pose
    SmartDashboard.putNumber("Drive/Odometry X (m)", pose.getX());
    SmartDashboard.putNumber("Drive/Odometry Y (m)", pose.getY());
    SmartDashboard.putNumber("Drive/Odometry Rotation (deg)", pose.getRotation().getDegrees());
  }

  @Override
  public void simulationPeriodic() {
    // In the IO layer pattern, simulation updates happen in updateInputs()
    // This method can be used for additional simulation-specific visualization if needed
  }
}
