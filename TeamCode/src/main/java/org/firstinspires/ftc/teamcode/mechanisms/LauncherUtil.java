package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Two-motor launcher with encoder-based velocity control.
 * Based on LaunchTestTeleOp from Ftc-Biobuzz.
 *
 * Hardware config names: "launchpollen", "launchnectar"
 * Speeds are in encoder ticks per second. Speed values live in RobotConstants.
 */
public class LauncherUtil {

    private DcMotorEx launchpollen;
    private DcMotorEx launchnectar;

    private int pollenTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
    private int nectarTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;

    public void init(HardwareMap hwMap) {
        launchpollen = hwMap.get(DcMotorEx.class, "launchpollen");
        launchnectar = hwMap.get(DcMotorEx.class, "launchnectar");

        for (DcMotorEx motor : new DcMotorEx[]{launchpollen, launchnectar}) {
            // Encoder-based velocity control
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.getMotorType().setTicksPerRev(RobotConstants.LAUNCHER_TICKS_PER_REV);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        stop();
    }

    // ---------------- Both motors ----------------

    /**
     * Run both launcher motors at the given velocity (ticks/sec)
     */
    public void setSpeed(int speed) {
        setPollenSpeed(speed);
        setNectarSpeed(speed);
    }

    /**
     * Turn the launcher on at RobotConstants.NEAR_LAUNCH_SPEED, or off if either motor is running
     */
    public void toggle() {
        if (isOn()) {
            stop();
        } else {
            setSpeed(RobotConstants.NEAR_LAUNCH_SPEED);
        }
    }

    /**
     * Set speed proportionally from 0..1 (e.g. a trigger value) up to RobotConstants.LAUNCHER_MAX_SPEED
     */
    public void setSpeedFraction(double fraction) {
        setSpeed(fractionToSpeed(fraction));
    }

    /**
     * Stop both launcher motors
     */
    public void stop() {
        setSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    /**
     * True if either motor has been told to run
     */
    public boolean isOn() {
        return isPollenOn() || isNectarOn();
    }

    // ---------------- Pollen motor ----------------

    /**
     * Run only the pollen launcher motor at the given velocity (ticks/sec)
     */
    public void setPollenSpeed(int speed) {
        pollenTargetSpeed = speed;
        launchpollen.setVelocity(speed);
    }

    /**
     * Set pollen speed proportionally from 0..1 up to RobotConstants.LAUNCHER_MAX_SPEED
     */
    public void setPollenSpeedFraction(double fraction) {
        setPollenSpeed(fractionToSpeed(fraction));
    }

    /**
     * Turn the pollen motor on at RobotConstants.NEAR_LAUNCH_SPEED, or off if it is already on
     */
    public void togglePollen() {
        setPollenSpeed(isPollenOn() ? RobotConstants.LAUNCHER_STOP_SPEED : RobotConstants.NEAR_LAUNCH_SPEED);
    }

    public void stopPollen() {
        setPollenSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    public boolean isPollenOn() {
        return pollenTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED;
    }

    public int getPollenTargetSpeed() {
        return pollenTargetSpeed;
    }

    /**
     * Current pollen motor velocity in ticks/sec
     */
    public double getPollenVelocity() {
        return launchpollen.getVelocity();
    }

    /**
     * Current pollen motor speed in RPM (0 when the pollen motor is off)
     */
    public double getPollenRpm() {
        return isPollenOn() ? toRpm(launchpollen) : 0.0;
    }

    // ---------------- Nectar motor ----------------

    /**
     * Run only the nectar launcher motor at the given velocity (ticks/sec)
     */
    public void setNectarSpeed(int speed) {
        nectarTargetSpeed = speed;
        launchnectar.setVelocity(speed);
    }

    /**
     * Set nectar speed proportionally from 0..1 up to RobotConstants.LAUNCHER_MAX_SPEED
     */
    public void setNectarSpeedFraction(double fraction) {
        setNectarSpeed(fractionToSpeed(fraction));
    }

    /**
     * Turn the nectar motor on at RobotConstants.NEAR_LAUNCH_SPEED, or off if it is already on
     */
    public void toggleNectar() {
        setNectarSpeed(isNectarOn() ? RobotConstants.LAUNCHER_STOP_SPEED : RobotConstants.NEAR_LAUNCH_SPEED);
    }

    public void stopNectar() {
        setNectarSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    public boolean isNectarOn() {
        return nectarTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED;
    }

    public int getNectarTargetSpeed() {
        return nectarTargetSpeed;
    }

    /**
     * Current nectar motor velocity in ticks/sec
     */
    public double getNectarVelocity() {
        return launchnectar.getVelocity();
    }

    /**
     * Current nectar motor speed in RPM (0 when the nectar motor is off)
     */
    public double getNectarRpm() {
        return isNectarOn() ? toRpm(launchnectar) : 0.0;
    }

    // ---------------- Helpers ----------------

    private static int fractionToSpeed(double fraction) {
        return (int) (fraction * RobotConstants.LAUNCHER_MAX_SPEED);
    }

    private static double toRpm(DcMotorEx motor) {
        double ticksPerRev = motor.getMotorType().getTicksPerRev();
        return (motor.getVelocity() / ticksPerRev) * 60.0;
    }
}
