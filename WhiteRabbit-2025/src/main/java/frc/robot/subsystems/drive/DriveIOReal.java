// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.AnalogGyro;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import frc.robot.Constants.DriveTrainConstants;

/**
 * Real hardware implementation of DriveIO.
 * Uses actual Talon motor controllers, encoders, and gyroscope.
 */
public class DriveIOReal implements DriveIO {
  private final Talon m_leftFront;
  private final Talon m_rightFront;
  private final DifferentialDrive m_drive;

  // Encoders for position feedback
  private final Encoder m_leftEncoder;
  private final Encoder m_rightEncoder;

  // Gyroscope for heading
  private final AnalogGyro m_gyro;

  public DriveIOReal() {
    // Initialize motor controllers
    m_leftFront = new Talon(DriveTrainConstants.kLeftFrontChannel);
    m_rightFront = new Talon(DriveTrainConstants.kRightFrontChannel);

    // Right side needs to be inverted for differential drive
    m_rightFront.setInverted(true);

    // Create differential drive
    m_drive = new DifferentialDrive(m_leftFront, m_rightFront);

    // Initialize encoders (DIO ports from Constants)
    m_leftEncoder = new Encoder(
        DriveTrainConstants.kLeftEncoderChannelA,
        DriveTrainConstants.kLeftEncoderChannelB,
        DriveTrainConstants.kLeftEncoderReversed
    );
    m_rightEncoder = new Encoder(
        DriveTrainConstants.kRightEncoderChannelA,
        DriveTrainConstants.kRightEncoderChannelB,
        DriveTrainConstants.kRightEncoderReversed
    );

    // Configure encoder distance per pulse
    m_leftEncoder.setDistancePerPulse(DriveTrainConstants.kEncoderDistancePerPulse);
    m_rightEncoder.setDistancePerPulse(DriveTrainConstants.kEncoderDistancePerPulse);

    // Initialize gyroscope
    m_gyro = new AnalogGyro(DriveTrainConstants.kGyroChannel);
  }

  @Override
  public void updateInputs(DriveIOInputs inputs) {
    // Update encoder positions and velocities
    inputs.leftPositionMeters = m_leftEncoder.getDistance();
    inputs.leftVelocityMetersPerSec = m_leftEncoder.getRate();
    inputs.rightPositionMeters = m_rightEncoder.getDistance();
    inputs.rightVelocityMetersPerSec = m_rightEncoder.getRate();

    // Update gyro angle
    inputs.gyroAngle = Rotation2d.fromDegrees(-m_gyro.getAngle());

    // Update motor outputs
    inputs.leftAppliedVolts = m_leftFront.get() * RobotController.getBatteryVoltage();
    inputs.rightAppliedVolts = m_rightFront.get() * RobotController.getBatteryVoltage();

    // Current draw - real hardware would measure this from PDH
    // For now, estimate based on output (real implementation would use PowerDistribution)
    inputs.leftCurrentAmps = Math.abs(m_leftFront.get()) * 20.0;
    inputs.rightCurrentAmps = Math.abs(m_rightFront.get()) * 20.0;
  }

  @Override
  public void setLeftVoltage(double volts) {
    m_leftFront.setVoltage(volts);
  }

  @Override
  public void setRightVoltage(double volts) {
    m_rightFront.setVoltage(volts);
  }

  @Override
  public void setTankDrive(double leftSpeed, double rightSpeed) {
    m_drive.tankDrive(leftSpeed, rightSpeed);
  }

  @Override
  public void stop() {
    m_drive.stopMotor();
  }
}
