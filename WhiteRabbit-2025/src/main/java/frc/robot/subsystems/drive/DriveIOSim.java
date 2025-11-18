// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.system.plant.DCMotor;
import edu.wpi.first.wpilibj.simulation.AnalogGyroSim;
import edu.wpi.first.wpilibj.simulation.DifferentialDrivetrainSim;
import edu.wpi.first.wpilibj.simulation.EncoderSim;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import frc.robot.Constants.DriveTrainConstants;

/**
 * Simulation implementation of DriveIO.
 * Uses WPILib physics simulation for realistic drivetrain behavior.
 */
public class DriveIOSim implements DriveIO {
  // Physics simulation model
  private final DifferentialDrivetrainSim m_driveSim;

  // Simulated sensors
  private final Encoder m_leftEncoder;
  private final Encoder m_rightEncoder;
  private final EncoderSim m_leftEncoderSim;
  private final EncoderSim m_rightEncoderSim;

  private final AnalogGyro m_gyro;
  private final AnalogGyroSim m_gyroSim;

  // Track applied voltages
  private double m_leftAppliedVolts = 0.0;
  private double m_rightAppliedVolts = 0.0;

  public DriveIOSim() {
    // Create the physics simulation model
    // Using CIM motors as an example - adjust DCMotor type based on your actual motors
    m_driveSim = new DifferentialDrivetrainSim(
        DCMotor.getCIM(DriveTrainConstants.kMotorsPerSide),  // Motors per side
        DriveTrainConstants.kGearRatio,                       // Gearing reduction
        DriveTrainConstants.kRobotMomentOfInertia,           // MOI (kg*m^2)
        DriveTrainConstants.kRobotMassKg,                    // Robot mass (kg)
        DriveTrainConstants.kWheelRadiusMeters,              // Wheel radius (m)
        DriveTrainConstants.kTrackWidthMeters,               // Track width (m)
        null  // No measurement noise for now (can add VecBuilder for realistic noise)
    );

    // Create simulated encoders (these don't represent real hardware in sim)
    m_leftEncoder = new Encoder(
        DriveTrainConstants.kLeftEncoderChannelA,
        DriveTrainConstants.kLeftEncoderChannelB
    );
    m_rightEncoder = new Encoder(
        DriveTrainConstants.kRightEncoderChannelA,
        DriveTrainConstants.kRightEncoderChannelB
    );

    m_leftEncoder.setDistancePerPulse(DriveTrainConstants.kEncoderDistancePerPulse);
    m_rightEncoder.setDistancePerPulse(DriveTrainConstants.kEncoderDistancePerPulse);

    // Create encoder simulation wrappers
    m_leftEncoderSim = new EncoderSim(m_leftEncoder);
    m_rightEncoderSim = new EncoderSim(m_rightEncoder);

    // Create simulated gyroscope
    m_gyro = new AnalogGyro(DriveTrainConstants.kGyroChannel);
    m_gyroSim = new AnalogGyroSim(m_gyro);
  }

  @Override
  public void updateInputs(DriveIOInputs inputs) {
    // Update the physics simulation with applied voltages
    m_driveSim.setInputs(m_leftAppliedVolts, m_rightAppliedVolts);

    // Advance the simulation by 20ms (one robot loop)
    m_driveSim.update(0.02);

    // Update simulated encoder readings from the physics model
    m_leftEncoderSim.setDistance(m_driveSim.getLeftPositionMeters());
    m_leftEncoderSim.setRate(m_driveSim.getLeftVelocityMetersPerSecond());
    m_rightEncoderSim.setDistance(m_driveSim.getRightPositionMeters());
    m_rightEncoderSim.setRate(m_driveSim.getRightVelocityMetersPerSecond());

    // Update simulated gyro reading (negated to match convention)
    m_gyroSim.setAngle(-m_driveSim.getHeading().getDegrees());

    // Populate inputs from simulated sensors
    inputs.leftPositionMeters = m_leftEncoder.getDistance();
    inputs.leftVelocityMetersPerSec = m_leftEncoder.getRate();
    inputs.rightPositionMeters = m_rightEncoder.getDistance();
    inputs.rightVelocityMetersPerSec = m_rightEncoder.getRate();

    inputs.gyroAngle = Rotation2d.fromDegrees(-m_gyro.getAngle());

    inputs.leftAppliedVolts = m_leftAppliedVolts;
    inputs.rightAppliedVolts = m_rightAppliedVolts;

    // Simulate current draw from the physics model
    inputs.leftCurrentAmps = Math.abs(m_driveSim.getLeftCurrentDrawAmps());
    inputs.rightCurrentAmps = Math.abs(m_driveSim.getRightCurrentDrawAmps());
  }

  @Override
  public void setLeftVoltage(double volts) {
    m_leftAppliedVolts = Math.max(-12.0, Math.min(12.0, volts));
  }

  @Override
  public void setRightVoltage(double volts) {
    m_rightAppliedVolts = Math.max(-12.0, Math.min(12.0, volts));
  }

  @Override
  public void setTankDrive(double leftSpeed, double rightSpeed) {
    // Convert speed percentage to voltage
    double batteryVoltage = RobotController.getBatteryVoltage();
    setLeftVoltage(leftSpeed * batteryVoltage);
    setRightVoltage(rightSpeed * batteryVoltage);
  }

  @Override
  public void stop() {
    m_leftAppliedVolts = 0.0;
    m_rightAppliedVolts = 0.0;
  }
}
