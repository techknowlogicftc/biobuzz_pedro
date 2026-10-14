package org.firstinspires.ftc.teamcode.opmodes.teleop;

/**
 * BaseLinearTeleOp - LinearOpMode TeleOp base class for two-player control (ported from Decode_Pedro to Pedro Pathing 3).
 *
 * Same architecture as Decode_Pedro:
 * - Alliance-agnostic design using AllianceConfig (thin Blue/Red subclasses)
 * - Starting pose comes from PoseHolder (set by autonomous), falling back to the alliance home pose
 * - Main loop calls one handler per subsystem, then displays telemetry
 *
 * Mechanisms use the shared utils in org.firstinspires.ftc.teamcode.mechanisms:
 * - LauncherUtil for the two launcher motors
 * - IntakeUtil for the intake motor
 * - LimelightUtil for AprilTag detection of the alliance goal tag
 *
 * GAMEPAD1 CONTROLS (Driver):
 * - Left stick: Forward/backward and strafe movement
 * - Right stick: Robot rotation
 * - Right trigger: Slow mode (RobotConstants.SLOW_MODE_MULTIPLIER)
 * - Left + right bumper (hold): Turn to face the alliance goal AprilTag (left stick still drives)
 * - X button: Toggle pollen launcher (on at RobotConstants.NEAR_LAUNCH_SPEED, press again to stop)
 * - Left bumper: Switch running pollen launcher to RobotConstants.FAR_LAUNCH_SPEED
 * - Y button: Toggle nectar launcher (on at RobotConstants.NEAR_LAUNCH_SPEED, press again to stop)
 * - Right bumper: Switch running nectar launcher to RobotConstants.FAR_LAUNCH_SPEED
 * - A button: Toggle intake (press again to stop)
 * - B button: Toggle outtake (press again to stop)
 *
 * Subclasses must implement:
 * - getAllianceConfig(): Provide alliance-specific configuration (Blue/Red)
 */

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.mechanisms.IntakeUtil;
import org.firstinspires.ftc.teamcode.mechanisms.LauncherUtil;
import org.firstinspires.ftc.teamcode.mechanisms.LimelightUtil;
import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.PoseHolder;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;
import org.firstinspires.ftc.teamcode.pedro.Constants;

public abstract class BaseLinearTeleOp extends LinearOpMode {
    // Hardware components
    protected Follower follower;                    // Main robot movement controller
    protected LauncherUtil launcher;                // Two-motor launcher
    protected IntakeUtil intake;                    // Intake motor
    protected LimelightUtil limelight;              // Limelight3A AprilTag camera

    // Alliance-specific configuration
    protected AllianceConfig allianceConfig;

    // Control states
    protected boolean slowMode = false;             // Tracks whether slow mode is held
    protected boolean aligning = false;             // Tracks whether goal alignment is active (both bumpers held)
    protected boolean aligned = false;              // Tracks whether the robot is facing the goal tag

    /**
     * Abstract method that subclasses must implement to provide alliance-specific configuration
     * @return AllianceConfig object with alliance-specific constants
     */
    protected abstract AllianceConfig getAllianceConfig();

    @Override
    public void runOpMode() throws InterruptedException {
        // INITIALIZATION PHASE (equivalent to init())
        // Get alliance-specific configuration
        allianceConfig = getAllianceConfig();

        // Initialize hardware components
        initializeHardware();

        telemetry.addData("Alliance", allianceConfig.getAllianceName());
        telemetry.addData("Status", "Initialized - Waiting for start");
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // START PHASE (equivalent to start())
        if (isStopRequested()) return;

        // MAIN LOOP PHASE (equivalent to loop())
        while (opModeIsActive()) {
            // Update the follower to get current robot position and handle movement
            follower.update();

            // Cache fresh AprilTag data for this loop
            //limelight.update();

            // MANUAL DRIVE CONTROL (GAMEPAD1)
            handleManualControl();

            // LAUNCHER CONTROL (GAMEPAD1)
            handleLauncherControl();

            // INTAKE CONTROL (GAMEPAD1)
            handleIntakeControl();

            // TELEMETRY DISPLAY
            displayTelemetry();
        }

        // Final cleanup when the opmode stops
        stopAllMotors();
    }

    /**
     * Initialize hardware. Override (and call super) to add this robot's mechanisms.
     */
    protected void initializeHardware() {
        launcher = new LauncherUtil();
        launcher.init(hardwareMap);

        intake = new IntakeUtil();
        intake.init(hardwareMap);

        // Track only this alliance's goal AprilTag
        //limelight = new LimelightUtil(hardwareMap);
        //limelight.setTargetTagId(allianceConfig.getTargetAprilTagId());

        // Create the follower object using our robot's hardware configuration
        follower = Constants.create(hardwareMap);

        // Check if we have a pose from autonomous mode, otherwise use alliance config
        Pose startingPose = PoseHolder.getCurrentPose();
        if (startingPose != null) {
            // Use the pose from autonomous mode
            follower.setPose(startingPose);
            telemetry.addData("Status", "Using pose from autonomous mode");
            telemetry.addData("Starting Pose", String.format("(%.1f, %.1f, %.1f°)",
                startingPose.x(), startingPose.y(), Math.toDegrees(startingPose.heading())));
        } else {
            // Use the default alliance config pose
            follower.setPose(allianceConfig.getAllianceHomePose());
            telemetry.addData("Status", "Using default alliance config pose");
        }

        // Update the follower to get current robot position
        follower.update();
    }

    /**
     * Handles manual driver control with speed adjustment
     */
    protected void handleManualControl() {
        // Slow mode while right trigger is held
        slowMode = gamepad1.right_trigger > RobotConstants.TRIGGER_DEADBAND;
        double speedMultiplier = slowMode ? RobotConstants.SLOW_MODE_MULTIPLIER : 1.0;

        // Forward and strafe are flipped so the driver's "front" is the robot's back end
        // (motor directions in Constants are unchanged, so autonomous and odometry are unaffected)
        double forward = applyDeadzone(gamepad1.left_stick_y) * speedMultiplier;    // Forward/backward movement
        double strafe = applyDeadzone(gamepad1.left_stick_x) * speedMultiplier;     // Left/right strafe movement
        double turn = -applyDeadzone(gamepad1.right_stick_x) * speedMultiplier;     // Rotation

        // Both bumpers held - Limelight takes over rotation to face the goal
        aligning = gamepad1.left_bumper && gamepad1.right_bumper;
        if (aligning) {
            //turn = handleAlignToGoal(turn);
        } else {
            aligned = false;
        }

        // Robot centric - robot moves relative to its current orientation
        follower.manual(forward, strafe, turn);
    }

    /**
     * Turns the robot to face the alliance goal AprilTag using the Limelight horizontal offset (tx).
     * Proportional control on tx, clamped to RobotConstants.ALIGN_MAX_TURN_POWER.
     *
     * @param driverTurn the driver's turn input, used when no tag is visible
     * @return turn power to send to the follower
     *//*
    protected double handleAlignToGoal(double driverTurn) {
        if (!limelight.hasTarget()) {
            // No tag in view - leave rotation to the driver so they can find it
            aligned = false;
            return driverTurn;
        }

        double tx = limelight.getTx();
        aligned = Math.abs(tx) <= RobotConstants.ALIGN_TOLERANCE_DEG;
        if (aligned) {
            return 0.0;
        }

        // tx > 0 means the tag is to the right; negative turn power turns the robot right
        double turnPower = Math.min(RobotConstants.ALIGN_MAX_TURN_POWER,
            Math.max(RobotConstants.ALIGN_MIN_TURN_POWER, Math.abs(tx) * RobotConstants.ALIGN_KP));
        return -Math.signum(tx) * turnPower;
    }*/

    /**
     * Handles launcher control
     */
    protected void handleLauncherControl() {
        // Read every edge each loop so a stale press is never picked up later
        boolean xPressed = gamepad1.xWasPressed();
        boolean yPressed = gamepad1.yWasPressed();
        boolean leftBumperPressed = gamepad1.leftBumperWasPressed();
        boolean rightBumperPressed = gamepad1.rightBumperWasPressed();

        // Pollen launcher: X toggles on (near speed) / off; left bumper switches a running pollen launcher to far speed
        if (xPressed) {
            if (launcher.isPollenOn()) {
                launcher.stopPollen();
            } else {
                launcher.setPollenSpeed(RobotConstants.NEAR_LAUNCH_SPEED);
            }
        } else if (leftBumperPressed && launcher.isPollenOn()) {
            launcher.setPollenSpeed(RobotConstants.FAR_LAUNCH_SPEED);
        }

        // Nectar launcher: Y toggles on (near speed) / off; right bumper switches a running nectar launcher to far speed
        if (yPressed) {
            if (launcher.isNectarOn()) {
                launcher.stopNectar();
            } else {
                launcher.setNectarSpeed(RobotConstants.NEAR_LAUNCH_SPEED);
            }
        } else if (rightBumperPressed && launcher.isNectarOn()) {
            launcher.setNectarSpeed(RobotConstants.FAR_LAUNCH_SPEED);
        }
    }

    /**
     * Handles intake control
     */
    protected void handleIntakeControl() {
        // Read every edge each loop so a stale press is never picked up later
        boolean aPressed = gamepad1.aWasPressed();
        boolean bPressed = gamepad1.bWasPressed();

        // A toggles intake on/off; switches straight from outtake to intake if outtake is running
        if (aPressed) {
            if (intake.getTargetSpeed() == RobotConstants.INTAKE_SPEED) {
                intake.stop();
            } else {
                intake.intake();
            }
        } else if (bPressed) {
            // B toggles outtake on/off; switches straight from intake to outtake if intake is running
            if (intake.getTargetSpeed() == RobotConstants.OUTTAKE_SPEED) {
                intake.stop();
            } else {
                intake.outtake();
            }
        }
    }

    /**
     * Displays telemetry information
     */
    protected void displayTelemetry() {
        Pose currentPose = follower.pose();
        telemetry.addData("Alliance", allianceConfig.getAllianceName());
        telemetry.addData("Current Position", String.format("(%.1f, %.1f)", currentPose.x(), currentPose.y()));
        telemetry.addData("Current Heading", String.format("%.1f°", normalizeAngleTo360(Math.toDegrees(currentPose.heading()))));
        telemetry.addData("Slow Mode", slowMode ? "ACTIVE" : "OFF");
        //telemetry.addData("Goal Align", !aligning ? "OFF" : (!limelight.hasTarget() ? "NO TAG" : (aligned ? "ALIGNED" : "TURNING")));

        // Launcher status (each motor is controlled separately)
        telemetry.addData("Pollen Launcher", launcherStatus(launcher.isPollenOn(),
            launcher.getPollenTargetSpeed(), launcher.getPollenVelocity()));
        telemetry.addData("Nectar Launcher", launcherStatus(launcher.isNectarOn(),
            launcher.getNectarTargetSpeed(), launcher.getNectarVelocity()));

        // Intake status
        int intakeSpeed = intake.getTargetSpeed();
        telemetry.addData("Intake", intakeSpeed == RobotConstants.INTAKE_STOP_SPEED ? "OFF" :
            (intakeSpeed == RobotConstants.OUTTAKE_SPEED ? "REVERSE" : "ON"));
        telemetry.addData("Intake Velocity", String.format("%.0f ticks/sec", intake.getVelocity()));

        // Limelight status
        /*telemetry.addData("Limelight", limelight.getStatusString());
        if (limelight.hasTarget()) {
            telemetry.addData("Goal Distance", String.format("%.1f in", limelight.getDistanceToTargetInches()));
            telemetry.addData("Goal tx", String.format("%.1f°", limelight.getTx()));
        }*/

        telemetry.addData("Gamepad1 Controls", String.format("X=Pollen Near(%d)/Stop, LB=Pollen Far(%d), Y=Nectar Near/Stop, RB=Nectar Far, RT=Slow, LB+RB=Align",
            RobotConstants.NEAR_LAUNCH_SPEED, RobotConstants.FAR_LAUNCH_SPEED));
        telemetry.addData("Intake Controls", "A=Intake On/Off, B=Outtake On/Off");
        telemetry.update();
    }

    /**
     * Telemetry text for one launcher motor, e.g. "ON (2080) AT SPEED - 2075 ticks/sec"
     */
    protected String launcherStatus(boolean on, int targetSpeed, double velocity) {
        if (!on) {
            return String.format("OFF - %.0f ticks/sec", velocity);
        }
        return String.format("ON (%d) %s - %.0f ticks/sec", targetSpeed,
            isLauncherAtSpeed(targetSpeed, velocity) ? "AT SPEED" : "WAITING", velocity);
    }

    /**
     * Checks if a launcher motor velocity is within tolerance of the target speed
     * (asymmetric: RobotConstants.LAUNCHER_VELOCITY_TOLERANCE_LOWER below, _UPPER above)
     */
    protected boolean isLauncherAtSpeed(int targetSpeed, double velocity) {
        double lowerBound = targetSpeed * (1.0 - RobotConstants.LAUNCHER_VELOCITY_TOLERANCE_LOWER);
        double upperBound = targetSpeed * (1.0 + RobotConstants.LAUNCHER_VELOCITY_TOLERANCE_UPPER);
        return velocity >= lowerBound && velocity <= upperBound;
    }

    /**
     * Stop all mechanisms and the drivetrain. Override (and call super) to stop this robot's mechanisms.
     */
    protected void stopAllMotors() {
        launcher.stop();
        intake.stop();
        //limelight.stop();
        follower.stop();
    }

    /**
     * Zero out small joystick values
     */
    protected double applyDeadzone(double value) {
        return Math.abs(value) < RobotConstants.STICK_DEADZONE ? 0.0 : value;
    }

    /**
     * Normalizes an angle to be within [0°, 360°) range for display purposes
     */
    protected double normalizeAngleTo360(double angleDegrees) {
        while (angleDegrees < 0) {
            angleDegrees += 360;
        }
        while (angleDegrees >= 360) {
            angleDegrees -= 360;
        }
        return angleDegrees;
    }
}
