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
  // private final Talon m_driveTrainLeftRear = new Talon(DriveTrainConstants.kLeftRearChannel);
  private final Talon m_driveTrainRightFront = new Talon(DriveTrainConstants.kRightFrontChannel);
  // private final Talon m_driveTrainRightRear = new Talon(DriveTrainConstants.kRightRearChannel);
  private final DifferentialDrive m_driveTrain; 
  
  /** Creates a new ExampleSubsystem. */
  public DriveTrainSubsystem() {
    // m_driveTrainLeftFront.addFollower(m_driveTrainLeftRear);

    // m_driveTrainRightFront.addFollower(m_driveTrainRightRear);

m_driveTrainRightFront.setInverted(true);
// <-- Use in case motors move in opposite directions

    m_driveTrain = new DifferentialDrive(m_driveTrainLeftFront, m_driveTrainRightFront);
  }
    public void tankDrive(double leftSpeed, double rightSpeed){
      m_driveTrain.tankDrive(leftSpeed, rightSpeed);
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
