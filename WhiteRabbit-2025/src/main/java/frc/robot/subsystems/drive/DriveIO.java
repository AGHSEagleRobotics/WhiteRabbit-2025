// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.drive;

import edu.wpi.first.math.geometry.Rotation2d;

/**
 * Hardware abstraction interface for the drivetrain.
 * This allows us to swap between real hardware and simulation implementations.
 */
public interface DriveIO {

  /** Container for all drivetrain inputs (sensor readings) */
  public static class DriveIOInputs {
    public double leftPositionMeters = 0.0;
    public double leftVelocityMetersPerSec = 0.0;
    public double rightPositionMeters = 0.0;
    public double rightVelocityMetersPerSec = 0.0;

    public Rotation2d gyroAngle = new Rotation2d();

    public double leftAppliedVolts = 0.0;
    public double rightAppliedVolts = 0.0;
    public double leftCurrentAmps = 0.0;
    public double rightCurrentAmps = 0.0;
  }

  /**
   * Updates the set of loggable inputs.
   * Should be called periodically to refresh sensor data.
   */
  public default void updateInputs(DriveIOInputs inputs) {}

  /**
   * Sets the voltage to apply to the left side of the drivetrain.
   * @param volts Voltage to apply (-12 to +12)
   */
  public default void setLeftVoltage(double volts) {}

  /**
   * Sets the voltage to apply to the right side of the drivetrain.
   * @param volts Voltage to apply (-12 to +12)
   */
  public default void setRightVoltage(double volts) {}

  /**
   * Sets the output percentage for both sides (for simple tank drive).
   * @param leftSpeed Left side speed (-1.0 to +1.0)
   * @param rightSpeed Right side speed (-1.0 to +1.0)
   */
  public default void setTankDrive(double leftSpeed, double rightSpeed) {}

  /**
   * Stops all motors.
   */
  public default void stop() {
    setLeftVoltage(0);
    setRightVoltage(0);
  }
}
