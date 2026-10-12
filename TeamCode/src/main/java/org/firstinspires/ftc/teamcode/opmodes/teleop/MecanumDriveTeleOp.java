package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Controls:
 * - Left Stick Y: Forward/Backward
 * - Left Stick X: Drift left/right (strafe)
 * - Right Stick X: Turn left/right
 * - Right Trigger (hold): Slow mode (1/4 speed)
 * - A (hold): Run only the rear left motor
 */
@TeleOp
public class MecanumDriveTeleOp extends OpMode {

    private MecanumDrive drive;


    @Override
    public void init() {
        drive = new MecanumDrive();
        drive.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Stick Y is negative when pushed forward, so negate it to make forward positive
        double forward = -applyDeadzone(gamepad1.left_stick_y, RobotConstants.STICK_DEADZONE);
        double strafe = applyDeadzone(gamepad1.left_stick_x, RobotConstants.STICK_DEADZONE);
        double turn = applyDeadzone(gamepad1.right_stick_x, RobotConstants.STICK_DEADZONE);

        boolean slowMode = gamepad1.right_trigger > RobotConstants.STICK_DEADZONE;
        double speedMultiplier = slowMode ? RobotConstants.SLOW_MODE_MULTIPLIER : 1.0;
        forward *= speedMultiplier;
        strafe *= speedMultiplier;
        turn *= speedMultiplier;

        if (gamepad1.a) {
            // Hold A to spin only the rear left motor
            drive.runRearLeft(RobotConstants.TEST_MOTOR_POWER);
        } else {
            drive.drive(forward, strafe, turn);
        }

        telemetry.addData("Rear Left Test (A)", gamepad1.a ? "RUNNING" : "off");
        telemetry.addData("Slow Mode (RT)", slowMode ? "ON (1/4 speed)" : "off");
        telemetry.addData("Forward", "%.2f", forward);
        telemetry.addData("Strafe", "%.2f", strafe);
        telemetry.addData("Turn", "%.2f", turn);
        telemetry.update();
    }

    private double applyDeadzone(double value, double deadzone) {
        if (Math.abs(value) < deadzone) {
            return 0.0;
        }
        return value;
    }

    @Override
    public void stop() {
        drive.stopMotors();
    }
}
