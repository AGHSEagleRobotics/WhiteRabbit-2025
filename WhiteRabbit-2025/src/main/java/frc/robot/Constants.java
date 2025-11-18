// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
  }

  public static class DriveTrainConstants {
    // Motor controller PWM channels
    public static final int kLeftFrontChannel = 0;
    public static final int kLeftRearChannel = 7;
    public static final int kRightFrontChannel = 1;
    public static final int kRightRearChannel = 8;

    // Encoder DIO channels
    public static final int kLeftEncoderChannelA = 0;
    public static final int kLeftEncoderChannelB = 1;
    public static final boolean kLeftEncoderReversed = false;
    public static final int kRightEncoderChannelA = 2;
    public static final int kRightEncoderChannelB = 3;
    public static final boolean kRightEncoderReversed = true;

    // Gyroscope analog channel
    public static final int kGyroChannel = 0;

    // Physical robot parameters (ADJUST THESE FOR YOUR ROBOT!)
    public static final double kWheelRadiusMeters = 0.0762; // 3 inches in meters
    public static final double kTrackWidthMeters = 0.6096; // 24 inches in meters
    public static final int kEncoderPPR = 360; // Pulses per revolution (adjust for your encoder)
    public static final double kGearRatio = 10.71; // Motor rotations per wheel rotation (adjust for your gearbox)

    // Calculate distance per encoder pulse
    public static final double kEncoderDistancePerPulse =
        (2 * Math.PI * kWheelRadiusMeters) / (kEncoderPPR * kGearRatio);

    // Robot mass and moment of inertia (for simulation)
    public static final double kRobotMassKg = 27.0; // ~60 lbs
    public static final double kRobotMomentOfInertia = 7.5; // kg*m^2
    public static final int kMotorsPerSide = 1; // Number of motors per side (2 for CIM, 1 for NEO, etc.)
  }
  
}
