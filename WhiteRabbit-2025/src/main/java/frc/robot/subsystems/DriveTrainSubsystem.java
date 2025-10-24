// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveTrainConstants;

public class DriveTrainSubsystem extends SubsystemBase {
  private final Talon m_driveTrainLeftFront = new Talon(DriveTrainConstants.kLeftFrontChannel);
  private final Talon m_driveTrainLeftRear = new Talon(DriveTrainConstants.kLeftFrontChannel);
  private final Talon m_driveTrainRightFront = new Talon(DriveTrainConstants.kLeftFrontChannel);
  private final Talon m_driveTrainRightRear = new Talon(DriveTrainConstants.kLeftFrontChannel);
  private final DifferentialDrive m_driveTrain; 
  
  /** Creates a new ExampleSubsystem. */
  public DriveTrainSubsystem() {
    m_driveTrainLeftFront.addFollower(m_driveTrainLeftRear);
    m_driveTrainRightFront.addFollower(m_driveTrainRightRear);

    m_driveTrain = new DifferentialDrive(m_driveTrainLeftFront, m_driveTrainRightFront);
  }

  /**
   * Example command factory method.
   *
   * @return a command
   */
  public Command exampleMethodCommand() {
    // Inline construction of command goes here.
    // Subsystem::RunOnce implicitly requires `this` subsystem.
    return runOnce(
        () -> {
          /* one-time action goes here */
        });
  }

  /**
   * An example method querying a boolean state of the subsystem (for example, a digital sensor).
   *
   * @return value of some boolean subsystem state, such as a digital sensor.
   */
  public boolean exampleCondition() {
    // Query some boolean state, such as a digital sensor.
    return false;
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}
