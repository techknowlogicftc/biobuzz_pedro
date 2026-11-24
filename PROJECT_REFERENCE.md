# FTC Project Reference

## Project overview

This repository contains the Java code for a middle-school FIRST Tech Challenge (FTC) robot. It controls the robot during:

- **Autonomous:** The robot follows field paths and performs actions without driver control.
- **TeleOp:** Two drivers use game controllers to drive and operate the mechanisms.

The project uses the official FTC SDK, Pedro Pathing for movement and positioning, and the Limelight 3A for AprilTag camera tracking.

## What the robot does

- Moves forward, backward, sideways, and turns using a four-wheel mecanum drivetrain.
- Collects and releases game pieces with an intake motor.
- Launches game pieces using Pollen and Nectar launcher motors.
- Opens and closes gate servos to feed game pieces.
- Raises and lowers a lift.
- Finds alliance goal tags using the Limelight camera.
- Uses odometry and planned paths to drive accurately to known positions.

## Important folders

- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/teleop/`
  - TeleOp programs for the Red and Blue alliances.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/auton/`
  - Autonomous routines for different starting positions and alliance sides.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mechanisms/`
  - Hardware helper classes for the drivetrain, intake, launchers, lift, servos, camera, and IMU.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedro/`
  - Pedro Pathing configuration and hardware calibration.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/RobotConstants.java`
  - Shared speeds, delays, tolerances, and field positions.
- `FtcRobotController/`
  - Official FTC Robot Controller app and FTC example programs.

## TeleOp controls

### Driver: Gamepad 1

- **Left joystick:** Move forward/backward and strafe left/right.
- **Right joystick:** Rotate the robot.
- **Right trigger:** Enter slow mode.
- **Back button:** Turn toward the alliance goal.
- **X button:** Start or stop the Pollen launcher.
- **B button:** Start or stop the Nectar launcher.
- **Left/right bumpers:** Adjust launcher speed.
- **D-pad left:** Open or close the Pollen gate.
- **D-pad right:** Open or close the Nectar gate.
- **Both bumpers held:** Drive toward a saved shooting position.

### Operator: Gamepad 2

- **Y button:** Toggle the intake on and off.
- **A button:** Toggle the outtake on and off.
- **Left/right bumpers:** Adjust intake speed.

## Autonomous behavior

Autonomous programs run a sequence of actions. A typical sequence may:

1. Start from a saved starting position.
2. Search for the alliance goal AprilTag cluster.
3. Start the appropriate launcher motor.
4. Feed game pieces through the intake and servo gate.
5. Stop the launcher and intake.
6. Follow a curved or straight path back to a parking position.

Separate routines support Red and Blue alliances, along with center and side starting configurations.

## Main code concepts

- `BaseLinearTeleOp` contains shared two-player TeleOp controls.
- `BaseLinearAutonomous` executes autonomous states in sequence.
- `AllianceConfig` supplies alliance-specific tags, poses, and launcher settings.
- `LaunchTarget` stores named field positions used by autonomous and TeleOp.
- `PoseHolder` passes the robot's final autonomous position into TeleOp.
- `MecanumDrive` combines forward, strafe, and turn commands into four motor commands.
- `LimelightUtil` reads camera detections and can track an AprilTag cluster.
- `Constants.java` configures Pedro Pathing and GoBILDA Pinpoint odometry.

## Red and Blue support

The Red and Blue programs share common logic. Their differences are handled through `AllianceConfig`, so the team can switch:

- Alliance name.
- Goal AprilTag IDs.
- Home and starting poses.
- Autonomous parking poses.
- Red or Blue shooting positions.

## Launcher velocity PIDF tuning

The launchers already use encoder-based velocity control: `LauncherUtil` sets each `DcMotorEx` to `RUN_USING_ENCODER` and requests a speed with `setVelocity(...)`. The PIDF changes configure the motor controller's velocity loop separately for Pollen and Nectar.

### Relevant files

- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/RobotConstants.java`
  - Stores the separate Pollen and Nectar PIDF constants.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/mechanisms/LauncherUtil.java`
  - Applies each motor's constants during initialization and provides active PIDF readback helpers.
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmodes/teleop/LaunchTestTeleOp.java`
  - Applies the constants and displays each flywheel's target and actual speed, speed error, RPM, and active PIDF values.

### Current starting values

| Launcher | P | I | D | F |
|---|---:|---:|---:|---:|
| Pollen | 1.2 | 0.0 | 0.0 | 12.0 |
| Nectar | 1.2 | 0.0 | 0.0 | 12.0 |

These are starting values, not confirmed tuned values. `RobotConstants.java` documents the feed-forward estimate `F = 32767.0 / maxTicksPerSec`; use a safely measured maximum speed for each motor rather than assuming these values are optimal.

### Testing notes

1. Secure the robot and make sure the flywheels and anything they could contact are clear.
2. `LaunchTestTeleOp` is currently marked `@Disabled`. To run it from the Driver Station, remove that annotation, then rebuild and install the app.
3. Use the triggers to test the Pollen and Nectar motors individually. Watch actual speed, target speed, speed error, RPM, and PIDF readback on the Driver Station.
4. Verify that both motors respond in the expected direction, track the target without sustained oscillation, and recover smoothly after a game piece is launched. Stop testing if anything behaves unexpectedly.
5. Tune the motors independently and record measured results before changing the constants. Do not assume a formula or values from another robot will work for this one.
