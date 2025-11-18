# WPILib Simulation Guide

This robot code now supports WPILib simulation with a hardware abstraction layer (IO Layer Pattern). You can test and develop robot code without physical hardware!

## Quick Start

### Running Simulation

1. **Via VSCode (Recommended):**
   - Press `Cmd+Shift+P` (macOS) or `Ctrl+Shift+P` (Windows/Linux)
   - Type "WPILib: Simulate Robot Code"
   - Select it and choose "sim" when prompted
   - The Simulation GUI will open automatically

2. **Via Command Line:**
   ```bash
   export JAVA_HOME=~/wpilib/2025/jdk
   ./gradlew simulateJava
   ```

### What You'll See

The WPILib Simulation GUI provides:
- **System Joysticks:** Connect your Xbox controller or use keyboard simulation
- **Robot State:** Enable/Disable, Teleop/Autonomous modes
- **SmartDashboard/Glass:** View telemetry data
- **Field2d Visualization:** See your robot's position and movement

## Architecture Overview

### IO Layer Pattern

The code uses a hardware abstraction layer that separates subsystem logic from hardware details:

```
DriveTrainSubsystem (business logic)
        ↓
    DriveIO (interface)
        ↓
   ┌────┴────┐
   ↓         ↓
DriveIOReal  DriveIOSim
(hardware)   (physics simulation)
```

### Key Files

| File | Purpose |
|------|---------|
| [`DriveIO.java`](src/main/java/frc/robot/subsystems/drive/DriveIO.java) | Interface defining motor/sensor operations |
| [`DriveIOReal.java`](src/main/java/frc/robot/subsystems/drive/DriveIOReal.java) | Real hardware implementation (Talon, Encoder, Gyro) |
| [`DriveIOSim.java`](src/main/java/frc/robot/subsystems/drive/DriveIOSim.java) | Physics simulation implementation |
| [`DriveTrainSubsystem.java`](src/main/java/frc/robot/subsystems/DriveTrainSubsystem.java) | Subsystem that uses DriveIO |
| [`RobotContainer.java`](src/main/java/frc/robot/RobotContainer.java) | Selects Real vs Sim IO based on `RobotBase.isReal()` |

## How It Works

### Real Robot Mode
When running on real hardware (`RobotBase.isReal() == true`):
- `DriveIOReal` is instantiated
- Uses actual Talon motor controllers (PWM ports 0, 1)
- Reads from physical encoders (DIO ports 0-3)
- Reads from analog gyroscope (Analog port 0)

### Simulation Mode
When running in simulation (`RobotBase.isReal() == false`):
- `DriveIOSim` is instantiated
- Uses `DifferentialDrivetrainSim` physics model
- Simulates motors with realistic dynamics (voltage → velocity)
- Updates encoder readings from physics calculation
- Simulates gyroscope heading from drivetrain rotation

## Telemetry Dashboard

The robot publishes data to SmartDashboard:

| Key | Description |
|-----|-------------|
| `Drive/Left Position (m)` | Left wheel distance traveled |
| `Drive/Right Position (m)` | Right wheel distance traveled |
| `Drive/Left Velocity (m/s)` | Left wheel speed |
| `Drive/Right Velocity (m/s)` | Right wheel speed |
| `Drive/Heading (deg)` | Robot heading from gyroscope |
| `Drive/Left Voltage` | Applied voltage to left motors |
| `Drive/Right Voltage` | Applied voltage to right motors |
| `Drive/Left Current (A)` | Left motor current draw |
| `Drive/Right Current (A)` | Right motor current draw |
| `Field` | Field2d visualization widget |

### Viewing Telemetry

**Option 1: Glass (Recommended for Simulation)**
```bash
~/wpilib/2025/tools/Glass
```
- Navigate to `NetworkTables` → `SmartDashboard`
- Drag widgets to the main window
- Double-click `Field` to see 2D robot position

**Option 2: Shuffleboard**
```bash
~/wpilib/2025/tools/shuffleboard
```
- Automatically displays SmartDashboard data
- Great for creating custom dashboards

## Tuning Robot Parameters

Edit [`Constants.java`](src/main/java/frc/robot/Constants.java) to match your actual robot:

```java
public static class DriveTrainConstants {
    // Physical measurements
    public static final double kWheelRadiusMeters = 0.0762; // 3 inches
    public static final double kTrackWidthMeters = 0.6096; // 24 inches

    // Encoder configuration
    public static final int kEncoderPPR = 360; // Pulses per revolution
    public static final double kGearRatio = 10.71; // Motor:Wheel ratio

    // Robot mass (for simulation physics)
    public static final double kRobotMassKg = 27.0; // ~60 lbs
    public static final int kMotorsPerSide = 1; // Adjust for your drivetrain
}
```

### How to Measure

1. **Wheel Radius:** Measure wheel diameter, divide by 2, convert to meters
2. **Track Width:** Measure distance between left/right wheel centers
3. **Encoder PPR:** Check encoder datasheet (common values: 360, 2048, 4096)
4. **Gear Ratio:** Count teeth on gears, or check gearbox spec sheet
5. **Robot Mass:** Weigh your robot without battery, add ~7kg for battery

## Control Scheme

Default Xbox controller mapping:
- **Left Stick Y-Axis:** Left side tank drive speed
- **Right Stick Y-Axis:** Right side tank drive speed

## Testing Workflow

### 1. Unit Testing in Simulation
```bash
export JAVA_HOME=~/wpilib/2025/jdk
./gradlew simulateJava
```
- Test drive commands
- Verify encoder readings
- Check gyroscope behavior

### 2. Develop Autonomous Routines
- Write autonomous commands using `getAverageDistanceMeters()` and `getHeadingDegrees()`
- Test in simulation before running on real robot
- Use Field2d to visualize paths

### 3. Deploy to Real Robot
```bash
export JAVA_HOME=~/wpilib/2025/jdk
./gradlew deploy
```
- Code automatically switches to `DriveIOReal`
- No changes needed to subsystem logic

## Adding More Subsystems

Follow the same IO Layer pattern for new subsystems:

### Example: Shooter Subsystem

1. **Create interface:**
   ```java
   public interface ShooterIO {
       public static class ShooterIOInputs {
           public double velocityRPM = 0.0;
           public double currentAmps = 0.0;
       }

       void updateInputs(ShooterIOInputs inputs);
       void setVoltage(double volts);
   }
   ```

2. **Implement real hardware:**
   ```java
   public class ShooterIOReal implements ShooterIO {
       private final CANSparkMax m_motor;
       // ... implementation
   }
   ```

3. **Implement simulation:**
   ```java
   public class ShooterIOSim implements ShooterIO {
       private final FlywheelSim m_sim;
       // ... implementation
   }
   ```

4. **Use in subsystem:**
   ```java
   public class ShooterSubsystem extends SubsystemBase {
       private final ShooterIO m_io;
       public ShooterSubsystem(ShooterIO io) {
           m_io = io;
       }
   }
   ```

## Troubleshooting

### Build Fails with "Requires JVM 17"
Make sure JAVA_HOME is set:
```bash
export JAVA_HOME=~/wpilib/2025/jdk
```

Add this to your `~/.zshrc` or `~/.bashrc` to make it permanent.

### Simulation GUI Doesn't Open
1. Check that desktop support is enabled in build.gradle (line 83-84)
2. Try running from VSCode instead of command line
3. Check console for error messages

### Robot Doesn't Move in Simulation
1. Verify joystick is connected in Simulation GUI → "System Joysticks"
2. Check that robot is enabled (click "TeleOp" and "Enable")
3. Verify controller is detected (should show in "USB Devices")

### Encoders Show Zero
- In simulation: Physics model might not be running (check updateInputs() is called)
- On real robot: Check DIO port connections and encoder wiring

### Field2d Shows Wrong Position
- Tune `kEncoderDistancePerPulse` in Constants.java
- Verify wheel radius and encoder PPR are correct

## Benefits of This Architecture

✅ **Test without hardware:** Develop code at home
✅ **Faster iteration:** No deploy/reboot cycle
✅ **Unit testable:** Easy to write JUnit tests with mock IO
✅ **Realistic physics:** WPILib physics engine models real dynamics
✅ **Zero subsystem changes:** Same code runs on real robot and sim
✅ **Team collaboration:** New members can learn without breaking robot

## References

- [WPILib Simulation Documentation](https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/introduction.html)
- [Physics Simulation Guide](https://docs.wpilib.org/en/stable/docs/software/wpilib-tools/robot-simulation/physics-sim.html)
- [Structuring Command-Based Projects](https://docs.wpilib.org/en/stable/docs/software/commandbased/structuring-command-based-project.html)

---

**Happy Simulating!** 🤖
