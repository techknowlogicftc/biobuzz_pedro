package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.LiftUtil;

/**
 * LiftTestTeleOp - A minimal TeleOp program for testing the lift motor
 *
 * TO ADJUST SPEEDS:
 * Edit RobotConstants.MAX_LIFT_SPEED: Max speed for joystick control
 *
 * CONTROLS:
 * - gamepad1.left_stick_y: Push up to raise, pull down to lower; release to stop
 */
@TeleOp(name = "Lift Test", group = "Test")
public class LiftTestTeleOp extends OpMode {

    private static final double STICK_DEADBAND = 0.05;  // Ignore stick values below this

    private LiftUtil lift;

    @Override
    public void init() {
        lift = new LiftUtil();
        lift.init(hardwareMap);

        telemetry.addData("Status", "Lift Test TeleOp Ready");
        telemetry.addData("Controls", "Left stick up/down = raise/lower");
        telemetry.update();
    }

    @Override
    public void start() {
        lift.stop();
    }

    @Override
    public void loop() {
        // Stick Y is negative when pushed up, so negate it: up = positive = raise
        double stickValue = -gamepad1.left_stick_y;
        if (Math.abs(stickValue) > STICK_DEADBAND) {
            lift.setSpeedFraction(stickValue);
        } else {
            lift.stop();
        }

        updateTelemetry();
    }

    /**
     * Update telemetry display
     */
    private void updateTelemetry() {
        telemetry.addData("Lift Position", String.format("%d ticks", lift.getPosition()));
        telemetry.addData("Target Speed", String.format("%d ticks/sec", lift.getTargetSpeed()));
        telemetry.addData("Actual Speed", String.format("%d ticks/sec", (int) lift.getVelocity()));
        telemetry.addData("---", "---");
        telemetry.addData("Controls", "Left stick up/down = raise/lower");
        telemetry.update();
    }

    @Override
    public void stop() {
        lift.stop();
    }
}
