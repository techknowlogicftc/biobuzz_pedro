package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * LaunchTestTeleOp - A minimal TeleOp program for testing launch speeds
 *
 * This program provides ONLY launcher functionality for testing and tuning:
 * - Toggle launcher motors on/off with gamepad1.y
 *
 * TO ADJUST SPEEDS:
 * Edit these constants in RobotConstants:
 * - RobotConstants.LAUNCH_TEST_SPEED_1: Speed used when the launcher is toggled on
 * - RobotConstants.NEAR_LAUNCH_SPEED: Full speed for normal launch (teleop)
 *
 * CONTROLS:
 * - gamepad1.y: Toggle launcher motors on/off
 * - gamepad1.x / a / b: Run launcher at RobotConstants.LAUNCH_TEST_SPEED_1 / RobotConstants.LAUNCH_TEST_SPEED_2 / RobotConstants.LAUNCH_TEST_SPEED_3
 * - gamepad1.right_trigger: Hold to run launcher proportionally up to RobotConstants.LAUNCHER_MAX_SPEED; release to stop
 */

@TeleOp(name = "Launch Test", group = "Test")
public class LaunchTestTeleOp extends OpMode {
    // Hardware components
    protected DcMotorEx launch1;      // Left launcher motor
    protected DcMotorEx launch2;      // Right launcher motor

    protected boolean launcherOn = false;  // Tracks whether launcher motors are on
    protected boolean leftTriggerActive = false;// Tracks whether right trigger is controlling speed
    protected boolean rightTriggerActive = false;
    @Override
    public void init() {
        // Initialize hardware components
        launch1 = hardwareMap.get(DcMotorEx.class, "launchpollen");
        launch2 = hardwareMap.get(DcMotorEx.class, "launchnectar");

        // Configure launcher motors for encoder-based velocity control
        launch1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch1.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
        launch2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launch2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Stop launcher motors
        launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);

        telemetry.addData("Status", "Launch Test TeleOp Ready");
        telemetry.addData("Controls", "Y=toggle launcher, X/A/B=set speed, RT=variable speed");
        telemetry.addData("Launcher", "OFF");
        telemetry.update();
    }

    @Override
    public void start() {
        // Reset state when opmode starts
        launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    @Override
    public void loop() {
        // Toggle launcher motors with Y button
        if (gamepad1.yWasPressed()) {
            launcherOn = !launcherOn;
            if (launcherOn && gamepad1.aWasPressed()) {
                launch1.setVelocity(RobotConstants.LAUNCH_TEST_SPEED_1);
                telemetry.addData("Status", "Pollen Launcher motors ON");
            } else if (launcherOn && gamepad1.bWasPressed()){
                launch2.setVelocity(RobotConstants.LAUNCH_TEST_SPEED_1);
                telemetry.addData("Status", "Nectar Launcher motors ON");
            }
        }


        // Right trigger sets launcher speed proportionally (0 to RobotConstants.LAUNCHER_MAX_SPEED)
        if (gamepad1.left_trigger > RobotConstants.TRIGGER_DEADBAND) {
            leftTriggerActive = true;
            setPollenLaunchSpeed((int) (gamepad1.left_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (leftTriggerActive) {
            // Trigger released - stop launcher
            leftTriggerActive = false;
            launcherOn = false;
            launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
            telemetry.addData("Status", "Left Trigger released - Launcher motors OFF");
        }

        if (gamepad1.right_trigger > RobotConstants.TRIGGER_DEADBAND) {
            rightTriggerActive = true;
            setNectarLaunchSpeed((int) (gamepad1.right_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (rightTriggerActive) {
            // Trigger released - stop launcher
            rightTriggerActive = false;
            launcherOn = false;
            launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
            telemetry.addData("Status", "Right Trigger released - Launcher motors OFF");
        }

        // Update telemetry
        updateTelemetry();
    }

    public  double getRpm(){
        double rpm = 0.0;
        if(launcherOn) {
            double ticksPerRev = launch1.getMotorType().getTicksPerRev();

            // Get velocity in ticks per second
            double ticksPerSec = launch1.getVelocity();

            // Convert ticks per second to Revolutions Per Minute (RPM)
            rpm = (ticksPerSec / ticksPerRev) * 60.0;
        }
        return rpm;
    }

    /**
     * Run both launcher motors at the given velocity
     */
    protected void setPollenLaunchSpeed(int speed) {
        launcherOn = true;
        launch1.setVelocity(speed);
        telemetry.addData("Status", String.format("Pollen Launcher set to %d ticks/sec", speed));
    }

    protected void setNectarLaunchSpeed(int speed) {
        launcherOn = true;
        launch2.setVelocity(speed);
        telemetry.addData("Status", String.format("Nectar Launcher set to %d ticks/sec", speed));
    }

    /**
     * Update telemetry display
     */
    protected void updateTelemetry() {
        telemetry.addData("Launcher", launcherOn ? "ON" : "OFF");
        telemetry.addData("Launcher Speed", launch1.getVelocity() > 0 ?
            String.format("%d ticks/sec", (int)launch1.getVelocity()) : "STOPPED");
        telemetry.addData("Current RPM", getRpm());
        telemetry.addData("ticksPerRev", launch1.getMotorType().getTicksPerRev());
        //telemetry.addData('ticksPerRev', launch1.getMotorType().getTicksPerRev());
        telemetry.addData("---", "---");

        telemetry.addData("Speeds:", "");
        //telemetry.addData("  Full Speed", String.format("%d ticks/sec", RobotConstants.NEAR_LAUNCH_SPEED));
        telemetry.addData("---", "---");
        telemetry.addData("Controls", "Y=toggle launcher, X/A/B=set speed, RT=variable speed");
        telemetry.update();
    }

    @Override
    public void stop() {
        // Cleanup when opmode stops
        launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
    }
}
