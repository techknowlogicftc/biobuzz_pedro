package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.mechanisms.IntakeUtil;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * IntakeTestTeleOp - A minimal TeleOp program for testing the intake motor
 *
 * TO ADJUST SPEEDS:
 * Edit these constants in RobotConstants:
 * - RobotConstants.INTAKE_SPEED / RobotConstants.OUTTAKE_SPEED
 * - RobotConstants.MAX_INTAKE_SPEED: Max speed for trigger control
 *
 * CONTROLS:
 * - gamepad1.y: Toggle intake on/off (at INTAKE_SPEED)
 * - gamepad1.a: Run intake inward
 * - gamepad1.b: Run intake outward
 * - gamepad1.x: Stop intake
 * - gamepad1.right_trigger / left_trigger: Hold to run in / out proportionally; release to stop
 */
@TeleOp(name = "Intake Test", group = "Test")
public class IntakeTestTeleOp extends OpMode {

    private IntakeUtil intake;

    private boolean triggerActive = false;  // Tracks whether a trigger is controlling speed

    @Override
    public void init() {
        intake = new IntakeUtil();
        intake.init(hardwareMap);

        telemetry.addData("Status", "Intake Test TeleOp Ready");
        telemetry.addData("Controls", "Y=toggle, A=in, B=out, X=stop, RT/LT=variable in/out");
        telemetry.update();
    }

    @Override
    public void start() {
        intake.stop();
    }

    @Override
    public void loop() {
        if (gamepad1.yWasPressed()) {
            intake.toggle();
        } else if (gamepad1.aWasPressed()) {
            intake.intake();
        } else if (gamepad1.bWasPressed()) {
            intake.outtake();
        } else if (gamepad1.xWasPressed()) {
            intake.stop();
        }

        // Triggers set intake speed proportionally: right = in, left = out
        double triggerValue = gamepad1.right_trigger - gamepad1.left_trigger;
        if (Math.abs(triggerValue) > RobotConstants.TRIGGER_DEADBAND) {
            triggerActive = true;
            intake.setSpeedFraction(triggerValue);
        } else if (triggerActive) {
            // Trigger released - stop intake
            triggerActive = false;
            intake.stop();
        }

        updateTelemetry();
    }

    /**
     * Update telemetry display
     */
    private void updateTelemetry() {
        telemetry.addData("Intake", intake.isOn() ? "ON" : "OFF");
        telemetry.addData("Target Speed", String.format("%d ticks/sec", intake.getTargetSpeed()));
        telemetry.addData("Actual Speed", String.format("%d ticks/sec", (int) intake.getVelocity()));
        telemetry.addData("Current RPM", String.format("%.0f", intake.getRpm()));
        telemetry.addData("---", "---");
        telemetry.addData("Controls", "Y=toggle, A=in, B=out, X=stop, RT/LT=variable in/out");
        telemetry.update();
    }

    @Override
    public void stop() {
        intake.stop();
    }
}
