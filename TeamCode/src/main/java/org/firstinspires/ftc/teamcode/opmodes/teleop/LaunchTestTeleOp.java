package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.mechanisms.ServoUtil;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

import java.util.Locale;

/**
 * LaunchTestTeleOp - Dedicated bench-testing and live PIDF calibration OpMode for launcher motors.
 *
 * DATE ADDED / UPDATED:
 * - 2026-11-23: Added live in-situ velocity PIDF tuning for the Pollen launcher motor,
 *   inspired by FlywheelTunerTutorial, allowing real-time adjustment of P and F gains
 *   from Gamepad 1 without recompiling and redeploying code.
 * - 2026-11-24: Added gate servo controls (RB=Pollen Gate, LB=Nectar Gate) and gate status
 *   telemetry so drivers can test ball feeding and recovery time during flywheel tuning.
 *
 * USAGE & CONTROLS (GAMEPAD 1):
 * ---------------------------------------------------------------------------------
 * 1. SPEED ACTIVATION:
 *    - [Y] Button: Toggle Pollen motor ON (fixed CENTER_POLLEN_LAUNCH_SPEED = 1125 ticks/s)
 *                  or OFF. Frees hands so you can tune using the D-Pad.
 *    - Left Trigger: Hold for variable Pollen speed (0 up to LAUNCHER_MAX_SPEED).
 *    - Right Trigger: Hold for variable Nectar speed (0 up to LAUNCHER_MAX_SPEED).
 *
 * 2. LIVE POLLEN PIDF TUNING & GATE CONTROL:
 *    - D-Pad UP / DOWN:    Increase / decrease P (Proportional gain) by current step size.
 *    - D-Pad RIGHT / LEFT: Increase / decrease F (Feedforward gain) by current step size.
 *    - Right Bumper (RB):  Toggle Pollen gate servo OPEN / CLOSED (Added: 2026-11-24).
 *    - Left Bumper (LB):   Toggle Nectar gate servo OPEN / CLOSED (Added: 2026-11-24).
 *    - [B] Button:         Cycle tuning step size [0.01, 0.05, 0.10, 0.50, 1.00, 5.00].
 *    - [X] Button:         Emergency reset of P, I, D, F back to RobotConstants defaults.
 *
 * 3. TELEMETRY DISPLAY (DRIVER STATION):
 *    - Target Velocity vs. Current Velocity (ticks/s)
 *    - Velocity Error (Target - Current)
 *    - Actual Motor RPM
 *    - Active P & F values and active Step Size
 *    - Pollen Gate State (OPEN / CLOSED)
 *
 * POST-TESTING INSTRUCTIONS (WHAT TO DO AFTER TESTING):
 * ---------------------------------------------------------------------------------
 * - DO NOT USE THIS OPMODE FOR ACTUAL MATCHES: This is strictly a diagnostic/tuning tool.
 * - Once you find the optimal P and F values on the Driver Station telemetry:
 *   1. Copy those final P and F values into RobotConstants.java (POLLEN_PIDF_P, POLLEN_PIDF_F).
 *   2. Match OpModes (Autonomous & TeleOp) automatically use LauncherUtil.java, which reads
 *      the tuned constants directly from RobotConstants.java.
 *   3. Uncomment '@Disabled' below to hide this test OpMode from the Driver Station list.
 */
@TeleOp(name = "Launch Test", group = "Test")
// After testing is complete, uncomment the @Disabled line below to hide this test OpMode:
// @Disabled
public class LaunchTestTeleOp extends OpMode {

    // Hardware components
    protected DcMotorEx launch1;      // "launchpollen"
    protected DcMotorEx launch2;      // "launchnectar"

    // --- [START 2026-11-24: Gate Servo Integration for Testing Feeder with Flywheel] ---
    protected ServoUtil servos;       // Gate servos ("pollen", "nectar")
    // --- [START 2026-11-24: Latch Pollen Gate Open After Reaching Speed] ---
    protected boolean pollenGateOpenedAtSpeed = false;
    // --- [END 2026-11-24: Latch Pollen Gate Open After Reaching Speed] ---
    // --- [END 2026-11-24: Gate Servo Integration for Testing Feeder with Flywheel] ---

    protected boolean launcherOn = false;
    protected boolean leftTriggerActive = false;
    protected boolean rightTriggerActive = false;

    protected int pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
    protected int nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;

    // --- [START 2026-11-23: Live PIDF Tuning Fields for Pollen Motor] ---
    // Live PIDF tuning coefficients for Pollen motor (initialized from RobotConstants)
    protected double pollenP = RobotConstants.POLLEN_PIDF_P;
    protected double pollenI = RobotConstants.POLLEN_PIDF_I;
    protected double pollenD = RobotConstants.POLLEN_PIDF_D;
    protected double pollenF = RobotConstants.POLLEN_PIDF_F;

    // Available step sizes selectable via B button
    protected final double[] stepSizes = {0.01, 0.05, 0.1, 0.5, 1.0, 5.0};
    protected int stepIndex = 2; // Default starting step size: 0.1
    // --- [END 2026-11-23: Live PIDF Tuning Fields for Pollen Motor] ---

    @Override
    public void init() {
        // Initialize hardware motors from hardware map
        launch1 = hardwareMap.get(DcMotorEx.class, "launchpollen");
        launch2 = hardwareMap.get(DcMotorEx.class, "launchnectar");

        // --- [START 2026-11-24: Initialize Gate Servos for Flywheel Testing] ---
        servos = new ServoUtil();
        servos.init(hardwareMap);
        // --- [END 2026-11-24: Initialize Gate Servos for Flywheel Testing] ---

        // Configure launcher motors for encoder-based velocity control
        launch1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch1.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
        launch1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        launch2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launch2.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
        launch2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // --- [START 2026-11-23: Set Launcher Velocity PIDF Coefficients] ---
        // Reset Pollen live values to constants at init
        pollenP = RobotConstants.POLLEN_PIDF_P;
        pollenI = RobotConstants.POLLEN_PIDF_I;
        pollenD = RobotConstants.POLLEN_PIDF_D;
        pollenF = RobotConstants.POLLEN_PIDF_F;

        launch1.setVelocityPIDFCoefficients(pollenP, pollenI, pollenD, pollenF);
        launch2.setVelocityPIDFCoefficients(
                RobotConstants.NECTAR_PIDF_P,
                RobotConstants.NECTAR_PIDF_I,
                RobotConstants.NECTAR_PIDF_D,
                RobotConstants.NECTAR_PIDF_F
        );
        // --- [END 2026-11-23: Set Launcher Velocity PIDF Coefficients] ---

        stopMotors();

        telemetry.addData("Status", "Launch Test TeleOp Ready");
        telemetry.addData("Controls", "Y=Toggle speed, D-Pad=Tune P/F, B=Step, LT/RT=Variable");
        telemetry.addData("Launcher", "OFF");
        telemetry.update();
    }

    @Override
    public void start() {
        stopMotors();
    }

    @Override
    public void loop() {
        // --- [START 2026-11-23: Live PIDF Tuning & Hands-Free Speed Controls] ---
        boolean pidfChanged = false;
        double currentStep = stepSizes[stepIndex];

        // D-Pad Up / Down adjusts Proportional gain (P)
        if (gamepad1.dpadUpWasPressed()) {
            pollenP += currentStep;
            pidfChanged = true;
        } else if (gamepad1.dpadDownWasPressed()) {
            pollenP = Math.max(0.0, pollenP - currentStep);
            pidfChanged = true;
        }

        // D-Pad Right / Left adjusts Feedforward gain (F)
        if (gamepad1.dpadRightWasPressed()) {
            pollenF += currentStep;
            pidfChanged = true;
        } else if (gamepad1.dpadLeftWasPressed()) {
            pollenF = Math.max(0.0, pollenF - currentStep);
            pidfChanged = true;
        }

        // B button cycles tuning step size
        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex + 1) % stepSizes.length;
        }

        // X button resets Pollen PIDF back to RobotConstants baseline defaults
        if (gamepad1.xWasPressed()) {
            pollenP = RobotConstants.POLLEN_PIDF_P;
            pollenI = RobotConstants.POLLEN_PIDF_I;
            pollenD = RobotConstants.POLLEN_PIDF_D;
            pollenF = RobotConstants.POLLEN_PIDF_F;
            pidfChanged = true;
        }

        // Apply new PIDF coefficients immediately to motor controller if updated
        if (pidfChanged) {
            launch1.setVelocityPIDFCoefficients(pollenP, pollenI, pollenD, pollenF);
        }

        // --- [START 2026-11-24: Manual Gate Servo Bumper Controls (RB / LB)] ---
        // Allows students to manually test ball feeding during flywheel speed tests
        // Right Bumper toggles Pollen gate servo (open/close)
        if (gamepad1.rightBumperWasPressed()) {
            servos.togglePollen();
        }

        // Left Bumper toggles Nectar gate servo (open/close)
        if (gamepad1.leftBumperWasPressed()) {
            servos.toggleNectar();
        }
        // --- [END 2026-11-24: Manual Gate Servo Bumper Controls (RB / LB)] ---

        // --- [START 2026-11-23: Speed Toggle Controls] ---
        // Y button toggles Pollen constant launch speed (CENTER_POLLEN_LAUNCH_SPEED = 1125 ticks/sec)
        // Keeps the gate physically closed during spin-up until it first reaches 1120-1125 ticks/s
        if (gamepad1.yWasPressed()) {
            if (pollenTargetSpeed == RobotConstants.LAUNCHER_STOP_SPEED) {
                servos.openPollen(); // Robot OPEN command keeps the physically closed gate in position while spinning up
                pollenGateOpenedAtSpeed = false;
                setPollenLaunchSpeed(RobotConstants.CENTER_POLLEN_LAUNCH_SPEED);
            } else {
                pollenGateOpenedAtSpeed = false;
                setPollenLaunchSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
                servos.openPollen(); // Reset to the physically closed position when stopping
            }
        }
        // --- [END 2026-11-23: Speed Toggle Controls] ---

        // --- [START 2026-11-24: Latch Pollen Gate Open After Reaching Speed] ---
        // Open the physical gate once the flywheel first enters the 1120-1125 ticks/s band.
        // Keep it open if feeding pollen causes the flywheel speed to drop; only Y=OFF closes it.
        double pollenVelocity = launch1.getVelocity();
        if (pollenTargetSpeed == RobotConstants.LAUNCHER_STOP_SPEED) {
            if (pollenGateOpenedAtSpeed) {
                servos.openPollen();
            }
            pollenGateOpenedAtSpeed = false;
        } else if (!pollenGateOpenedAtSpeed
                && pollenVelocity >= RobotConstants.POLLEN_GATE_OPEN_LOWER_SPEED
                && pollenVelocity <= RobotConstants.POLLEN_GATE_OPEN_UPPER_SPEED) {
            servos.closePollen(); // Closed command is physically open for this servo mounting
            pollenGateOpenedAtSpeed = true;
        }
        // --- [END 2026-11-24: Latch Pollen Gate Open After Reaching Speed] ---

        // Left trigger sets Pollen launcher speed proportionally (0 to LAUNCHER_MAX_SPEED)
        if (gamepad1.left_trigger > RobotConstants.TRIGGER_DEADBAND) {
            leftTriggerActive = true;
            setPollenLaunchSpeed((int) (gamepad1.left_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (leftTriggerActive) {
            leftTriggerActive = false;
            setPollenLaunchSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
        }

        // Right trigger sets Nectar launcher speed proportionally (0 to LAUNCHER_MAX_SPEED)
        if (gamepad1.right_trigger > RobotConstants.TRIGGER_DEADBAND) {
            rightTriggerActive = true;
            setNectarLaunchSpeed((int) (gamepad1.right_trigger * RobotConstants.LAUNCHER_MAX_SPEED));
        } else if (rightTriggerActive) {
            rightTriggerActive = false;
            setNectarLaunchSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
        }

        // Global launcher status flag
        launcherOn = (pollenTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED)
                || (nectarTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED);

        updateTelemetry();
    }

    protected void stopMotors() {
        launcherOn = false;
        pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
        nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
        launch1.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        launch2.setVelocity(RobotConstants.LAUNCHER_STOP_SPEED);
        // --- [START 2026-11-24: Reset Pollen Gate When Stopping] ---
        pollenGateOpenedAtSpeed = false;
        servos.openPollen();
        // --- [END 2026-11-24: Reset Pollen Gate When Stopping] ---
    }

    public double getMotorRpm(DcMotorEx motor) {
        double ticksPerRev = motor.getMotorType().getTicksPerRev();
        if (ticksPerRev <= 0) {
            return 0.0;
        }
        return (motor.getVelocity() / ticksPerRev) * 60.0;
    }

    protected void setPollenLaunchSpeed(int speed) {
        pollenTargetSpeed = speed;
        launch1.setVelocity(speed);
    }

    protected void setNectarLaunchSpeed(int speed) {
        nectarTargetSpeed = speed;
        launch2.setVelocity(speed);
    }

    protected void updateTelemetry() {
        double pollenVel = launch1.getVelocity();
        double nectarVel = launch2.getVelocity();

        // --- [START 2026-11-23: Real-Time Velocity, Error, and PIDF Tuning Telemetry] ---
        telemetry.addData("Launcher", launcherOn ? "RUNNING" : "STOPPED");
        telemetry.addLine("--------------------------------");
        telemetry.addData("Target Velocity", "%d ticks/s", pollenTargetSpeed);
        telemetry.addData("Current Velocity", String.format(Locale.US, "%.1f ticks/s", pollenVel));
        telemetry.addData("Velocity Error", String.format(Locale.US, "%.1f ticks/s", pollenTargetSpeed - pollenVel));
        telemetry.addData("Pollen RPM", String.format(Locale.US, "%.1f RPM", getMotorRpm(launch1)));
        telemetry.addLine("--------------------------------");
        telemetry.addData("Tuning P (D-Pad U/D)", String.format(Locale.US, "%.4f", pollenP));
        telemetry.addData("Tuning F (D-Pad R/L)", String.format(Locale.US, "%.4f", pollenF));
        telemetry.addData("Step Size (B button)", String.format(Locale.US, "%.4f", stepSizes[stepIndex]));
        telemetry.addLine("--------------------------------");
        // --- [START 2026-11-24: Gate Servo Status Telemetry] ---
        telemetry.addData("Pollen Gate", servos.isPollenOpen()
                ? "ROBOT OPEN / PHYSICALLY CLOSED"
                : "ROBOT CLOSED / PHYSICALLY OPEN");
        // --- [END 2026-11-24: Gate Servo Status Telemetry] ---
        telemetry.addData("Nectar Speed", String.format(Locale.US, "%.0f / %d ticks/s", nectarVel, nectarTargetSpeed));
        telemetry.addData("Controls", "Y=Pollen, RB=Pollen Gate, X=Reset, LT/RT=Trigger");
        telemetry.update();
        // --- [END 2026-11-23: Real-Time Velocity, Error, and PIDF Tuning Telemetry] ---
    }

    @Override
    public void stop() {
        stopMotors();
    }
}
