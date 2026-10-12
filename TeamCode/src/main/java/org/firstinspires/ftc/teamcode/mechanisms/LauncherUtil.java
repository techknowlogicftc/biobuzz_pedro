package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Two-motor launcher with encoder-based velocity control.
 * Based on LaunchTestTeleOp from Ftc-Biobuzz.
 *
 * Hardware config names: "launchleft", "launchright"
 * Speeds are in encoder ticks per second. Speed values live in RobotConstants.
 */
public class LauncherUtil {

    private DcMotorEx leftMotor;
    private DcMotorEx rightMotor;

    private int leftTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;
    private int rightTargetSpeed = RobotConstants.LAUNCHER_STOP_SPEED;

    public void init(HardwareMap hwMap) {
        leftMotor = hwMap.get(DcMotorEx.class, "launchleft");
        rightMotor = hwMap.get(DcMotorEx.class, "launchright");

        for (DcMotorEx motor : new DcMotorEx[]{leftMotor, rightMotor}) {
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
        setLeftSpeed(speed);
        setRightSpeed(speed);
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
        return isLeftOn() || isRightOn();
    }

    // ---------------- Left motor ----------------

    /**
     * Run only the left launcher motor at the given velocity (ticks/sec)
     */
    public void setLeftSpeed(int speed) {
        leftTargetSpeed = speed;
        leftMotor.setVelocity(speed);
    }

    /**
     * Set left speed proportionally from 0..1 up to RobotConstants.LAUNCHER_MAX_SPEED
     */
    public void setLeftSpeedFraction(double fraction) {
        setLeftSpeed(fractionToSpeed(fraction));
    }

    /**
     * Turn the left motor on at RobotConstants.NEAR_LAUNCH_SPEED, or off if it is already on
     */
    public void toggleLeft() {
        setLeftSpeed(isLeftOn() ? RobotConstants.LAUNCHER_STOP_SPEED : RobotConstants.NEAR_LAUNCH_SPEED);
    }

    public void stopLeft() {
        setLeftSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    public boolean isLeftOn() {
        return leftTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED;
    }

    public int getLeftTargetSpeed() {
        return leftTargetSpeed;
    }

    /**
     * Current left motor velocity in ticks/sec
     */
    public double getLeftVelocity() {
        return leftMotor.getVelocity();
    }

    /**
     * Current left motor speed in RPM (0 when the left motor is off)
     */
    public double getLeftRpm() {
        return isLeftOn() ? toRpm(leftMotor) : 0.0;
    }

    // ---------------- Right motor ----------------

    /**
     * Run only the right launcher motor at the given velocity (ticks/sec)
     */
    public void setRightSpeed(int speed) {
        rightTargetSpeed = speed;
        rightMotor.setVelocity(speed);
    }

    /**
     * Set right speed proportionally from 0..1 up to RobotConstants.LAUNCHER_MAX_SPEED
     */
    public void setRightSpeedFraction(double fraction) {
        setRightSpeed(fractionToSpeed(fraction));
    }

    /**
     * Turn the right motor on at RobotConstants.NEAR_LAUNCH_SPEED, or off if it is already on
     */
    public void toggleRight() {
        setRightSpeed(isRightOn() ? RobotConstants.LAUNCHER_STOP_SPEED : RobotConstants.NEAR_LAUNCH_SPEED);
    }

    public void stopRight() {
        setRightSpeed(RobotConstants.LAUNCHER_STOP_SPEED);
    }

    public boolean isRightOn() {
        return rightTargetSpeed != RobotConstants.LAUNCHER_STOP_SPEED;
    }

    public int getRightTargetSpeed() {
        return rightTargetSpeed;
    }

    /**
     * Current right motor velocity in ticks/sec
     */
    public double getRightVelocity() {
        return rightMotor.getVelocity();
    }

    /**
     * Current right motor speed in RPM (0 when the right motor is off)
     */
    public double getRightRpm() {
        return isRightOn() ? toRpm(rightMotor) : 0.0;
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
