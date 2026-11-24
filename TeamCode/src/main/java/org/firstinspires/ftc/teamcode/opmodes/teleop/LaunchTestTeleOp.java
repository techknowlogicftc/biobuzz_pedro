package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * LaunchTestTeleOp - A minimal TeleOp program for testing launch speeds and PIDF tuning
 *
 * CONTROLS:
 * - gamepad1.left_trigger: Hold to run Pollen ("launchpollen") proportionally up to LAUNCHER_MAX_SPEED
 * - gamepad1.right_trigger: Hold to run Nectar ("launchnectar") proportionally up to LAUNCHER_MAX_SPEED
 */
@TeleOp(name = "Launch Test", group = "Test")
// After testing, uncomment @Disabled and restore its import to hide this test OpMode from the Driver Station.
// @Disabled
public class LaunchTestTeleOp extends OpMode {
    // Hardware components
    protected DcMotorEx launch1;      // "launchpollen"
    protected DcMotorEx launch2;      // "launchnectar"

    protected boolean launcherOn = false;
    protected boolean leftTriggerActive = false;
    protected boolean rightTriggerActive = false;

    protected int pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
    protected int nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;

    @Override
    public void init() {
        // Initialize hardware components
        launch1 = hardwareMap.get(DcMotorEx.class, "launchpollen");
        launch2 = hardwareMap.get(DcMotorEx.class, "launchnectar");

        // Configure launcher motors for encoder-based velocity control
        launch1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch1.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
        launch1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launch2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch2.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
        launch2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Added 2026-11-23: apply and report the launcher velocity PIDF coefficients.
        launch1.setVelocityPIDFCoefficients(
                RobotConstants.POLLEN_PIDF_P,
                RobotConstants.POLLEN_PIDF_I,
                RobotConstants.POLLEN_PIDF_D,
                RobotConstants.POLLEN_PIDF_F
        );
        launch2.setVelocityPIDFCoefficients(
                RobotConstants.NECTAR_PIDF_P,
                RobotConstants.NECTAR_PIDF_I,
                RobotConstants.NECTAR_PIDF_D,
                RobotConstants.NECTAR_PIDF_F
        );

        stopMotors();

        telemetry.addData("Status", "Launch Test TeleOp Ready");
        telemetry.addData("Controls", "Y=toggle launcher, X/A/B=set speed, RT=variable speed");
        telemetry.addData("Launcher", "OFF");
        telemetry.update();
    }

    @Override
    public void start() {
        stopMotors();
    }

    @Override
    public void loop() {
        // Left trigger sets Pollen launcher speed proportionally (0 to LAUNCHER_MAX_SPEED)
        if (gamepad1.left_trigger > RobotConstants.TRIGGER_DEADBAND) {
            leftTriggerActive = true;
            setPollenLaunchSpeed((int) (gamepad1.left_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (leftTriggerActive) {
            leftTriggerActive = false;
            pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
            launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
            launcherOn = (nectarTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED);
            telemetry.addData("Status", "Left Trigger released - Pollen OFF");
        }

        // Right trigger sets Nectar launcher speed proportionally (0 to LAUNCHER_MAX_SPEED)
        if (gamepad1.right_trigger > RobotConstants.TRIGGER_DEADBAND) {
            rightTriggerActive = true;
            setNectarLaunchSpeed((int) (gamepad1.right_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (rightTriggerActive) {
            rightTriggerActive = false;
            nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
            launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
            launcherOn = (pollenTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED);
            telemetry.addData("Status", "Right Trigger released - Nectar OFF");
        }

        updateTelemetry();
    }

    protected void stopMotors() {
        launcherOn = false;
        pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
        nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
        launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    public double getMotorRpm(DcMotorEx motor) {
        double ticksPerRev = motor.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) {
            return 0.0;
        }
        return (motor.getVelocity() / ticksPerRev) * 60.0;
    }

    protected void setPollenLaunchSpeed(int speed) {
        launcherOn = true;
        pollenTargetSpeed = speed;
        launch1.setVelocity(speed);
        telemetry.addData("Status", String.format("Pollen target: %d ticks/sec", speed));
    }

    protected void setNectarLaunchSpeed(int speed) {
        launcherOn = true;
        nectarTargetSpeed = speed;
        launch2.setVelocity(speed);
        telemetry.addData("Status", String.format("Nectar target: %d ticks/sec", speed));
    }

    protected void updateTelemetry() {
        double pollenVel = launch1.getVelocity();
        double nectarVel = launch2.getVelocity();

        PIDFCoefficients pollenPidf = launch1.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients nectarPidf = launch2.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Launcher", launcherOn ? "ON" : "OFF");

        telemetry.addData("Pollen Speed", "%.0f / %d ticks/s (err %.0f)",
                pollenVel, pollenTargetSpeed, pollenTargetSpeed - pollenVel);
        telemetry.addData("Pollen RPM", "%.1f", getMotorRpm(launch1));
        telemetry.addData("Pollen PIDF", "P=%.3f I=%.3f D=%.3f F=%.2f",
                pollenPidf.p, pollenPidf.i, pollenPidf.d, pollenPidf.f);

        telemetry.addData("Nectar Speed", "%.0f / %d ticks/s (err %.0f)",
                nectarVel, nectarTargetSpeed, nectarTargetSpeed - nectarVel);
        telemetry.addData("Nectar RPM", "%.1f", getMotorRpm(launch2));
        telemetry.addData("Nectar PIDF", "P=%.3f I=%.3f D=%.3f F=%.2f",
                nectarPidf.p, nectarPidf.i, nectarPidf.d, nectarPidf.f);

        telemetry.addData("Controls", "LT=Pollen speed, RT=Nectar speed");
        telemetry.update();
    }

    @Override
    public void stop() {
        stopMotors();
    }
}
