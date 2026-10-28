package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Single-motor lift with encoder-based velocity control.
 *
 * Hardware config name: "lift"
 * Speeds are in encoder ticks per second. Positive = up, negative = down.
 * If the lift moves the wrong way, flip the sign of RobotConstants.LIFT_UP_SPEED and RobotConstants.LIFT_DOWN_SPEED.
 */
public class LiftUtil {

    private DcMotorEx liftMotor;

    private int targetSpeed = RobotConstants.LIFT_STOP_SPEED;

    public void init(HardwareMap hwMap) {
        liftMotor = hwMap.get(DcMotorEx.class, "lift");

        // Encoder-based velocity control; BRAKE so the lift holds when stopped
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stop();
    }

    /**
     * Run the lift motor at the given velocity (ticks/sec)
     */
    public void setSpeed(int speed) {
        targetSpeed = speed;
        liftMotor.setVelocity(speed);
    }

    /**
     * Set speed proportionally from -1..1 (e.g. a joystick value) up to RobotConstants.MAX_LIFT_SPEED
     */
    public void setSpeedFraction(double fraction) {
        setSpeed((int) (fraction * RobotConstants.MAX_LIFT_SPEED));
    }

    /**
     * Raise the lift at RobotConstants.LIFT_UP_SPEED
     */
    public void up() {
        setSpeed(RobotConstants.LIFT_UP_SPEED);
    }

    /**
     * Lower the lift at RobotConstants.LIFT_DOWN_SPEED
     */
    public void down() {
        setSpeed(RobotConstants.LIFT_DOWN_SPEED);
    }

    /**
     * Stop the lift motor
     */
    public void stop() {
        setSpeed(RobotConstants.LIFT_STOP_SPEED);
    }

    public boolean isOn() {
        return targetSpeed != RobotConstants.LIFT_STOP_SPEED;
    }

    public int getTargetSpeed() {
        return targetSpeed;
    }

    /**
     * Current lift encoder position in ticks
     */
    public int getPosition() {
        return liftMotor.getCurrentPosition();
    }

    /**
     * Current lift motor velocity in ticks/sec
     */
    public double getVelocity() {
        return liftMotor.getVelocity();
    }

    /**
     * Current lift motor speed in RPM (0 when the lift is stopped)
     */
    public double getRpm() {
        if (!isOn()) {
            return 0.0;
        }
        double ticksPerRev = liftMotor.getMotorType().getTicksPerRev();
        return (liftMotor.getVelocity() / ticksPerRev) * 60.0;
    }
}
