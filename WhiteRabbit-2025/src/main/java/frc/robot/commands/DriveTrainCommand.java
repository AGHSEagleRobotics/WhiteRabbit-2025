// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;
import java.util.function.Supplier;
import frc.robot.subsystems.DriveTrainSubsystem;
import edu.wpi.first.units.measure.Power;
import edu.wpi.first.wpilibj2.command.Command;

/** An example command that uses an example subsystem. */
public class DriveTrainCommand extends Command {
  
  private final DriveTrainSubsystem m_driveTrainSubsystem;

  private final Supplier<Double> m_leftY;
  private final Supplier<Double> m_rightY;
 
  public DriveTrainCommand(
      DriveTrainSubsystem driveTrainSubsystem,
      Supplier<Double> leftY, 
      Supplier<Double> rightY) {
    m_driveTrainSubsystem = driveTrainSubsystem;

    m_leftY = leftY;
    m_rightY = rightY;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_driveTrainSubsystem);
  }


// Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_driveTrainSubsystem.tankDrive(m_leftY.get(),m_rightY.get());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_driveTrainSubsystem.tankDrive(0,0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}


