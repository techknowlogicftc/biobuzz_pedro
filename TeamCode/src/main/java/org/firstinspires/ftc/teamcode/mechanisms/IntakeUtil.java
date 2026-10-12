package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Single-motor intake with encoder-based velocity control.
 *
 * Hardware config name: "intake"
 * Speeds are in encoder ticks per second. Positive = pull in, negative = push out.
 * If the intake spins the wrong way, flip the sign of RobotConstants.INTAKE_SPEED and RobotConstants.OUTTAKE_SPEED.
 */
public class IntakeUtil {

    private DcMotorEx intakeMotor;

    private int targetSpeed = RobotConstants.INTAKE_STOP_SPEED;

    public void init(HardwareMap hwMap) {
        intakeMotor = hwMap.get(DcMotorEx.class, "intake");

        // Encoder-based velocity control
        intakeMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        stop();
    }

    /**
     * Run the intake motor at the given velocity (ticks/sec)
     */
    public void setSpeed(int speed) {
        targetSpeed = speed;
        intakeMotor.setVelocity(speed);
    }

    /**
     * Set speed proportionally from -1..1 (e.g. a trigger value) up to RobotConstants.MAX_INTAKE_SPEED
     */
    public void setSpeedFraction(double fraction) {
        setSpeed((int) (fraction * RobotConstants.MAX_INTAKE_SPEED));
    }

    /**
     * Run the intake inward at RobotConstants.INTAKE_SPEED
     */
    public void intake() {
        setSpeed(RobotConstants.INTAKE_SPEED);
    }

    /**
     * Run the intake outward at RobotConstants.OUTTAKE_SPEED
     */
    public void outtake() {
        setSpeed(RobotConstants.OUTTAKE_SPEED);
    }

    /**
     * Turn the intake on at RobotConstants.INTAKE_SPEED, or off if it is already running
     */
    public void toggle() {
        if (isOn()) {
            stop();
        } else {
            intake();
        }
    }

    /**
     * Stop the intake motor
     */
    public void stop() {
        setSpeed(RobotConstants.INTAKE_STOP_SPEED);
    }

    public boolean isOn() {
        return targetSpeed != RobotConstants.INTAKE_STOP_SPEED;
    }

    public int getTargetSpeed() {
        return targetSpeed;
    }

    /**
     * Current intake motor velocity in ticks/sec
     */
    public double getVelocity() {
        return intakeMotor.getVelocity();
    }

    /**
     * Current intake motor speed in RPM (0 when the intake is off)
     */
    public double getRpm() {
        if (!isOn()) {
            return 0.0;
        }
        double ticksPerRev = intakeMotor.getMotorType().getTicksPerRev();
        return (intakeMotor.getVelocity() / ticksPerRev) * 60.0;
    }
}
