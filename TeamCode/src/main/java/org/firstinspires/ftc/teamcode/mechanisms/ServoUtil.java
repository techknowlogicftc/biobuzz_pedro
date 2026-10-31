package org.firstinspires.ftc.teamcode.mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;

/**
 * Two gate servos that feed the pollen and nectar launchers.
 *
 * Hardware config names: "pollen", "nectar"
 * Positions are 0..1. Open/closed positions live in RobotConstants.
 * If a gate moves the wrong way, swap its OPEN and CLOSED positions in RobotConstants.
 */
public class ServoUtil {

    private Servo pollenServo;
    private Servo nectarServo;

    private boolean pollenOpen = false;
    private boolean nectarOpen = false;

    public void init(HardwareMap hwMap) {
        pollenServo = hwMap.get(Servo.class, "pollen");
        nectarServo = hwMap.get(Servo.class, "nectar");

        openPollen();
    }

    // ---------------- Both servos ----------------

    /**
     * Open both gates
     */
    public void open() {
        openPollen();
        openNectar();
    }

    /**
     * Close both gates
     */
    public void close() {
        closePollen();
        closeNectar();
    }

    // ---------------- Pollen servo ----------------

    public void openPollen() {
        pollenServo.setPosition(RobotConstants.POLLEN_SERVO_OPEN_POSITION);
        pollenOpen = true;
    }

    public void closePollen() {
        pollenServo.setPosition(RobotConstants.POLLEN_SERVO_CLOSED_POSITION);
        pollenOpen = false;
    }

    /**
     * Open the pollen gate if it is closed, or close it if it is open
     */
    public void togglePollen() {
        if (pollenOpen) {
            closePollen();
        } else {
            openPollen();
        }
    }

    public boolean isPollenOpen() {
        return pollenOpen;
    }

    /**
     * Last commanded pollen servo position (servos do not report their actual position)
     */
    public double getPollenPosition() {
        return pollenServo.getPosition();
    }

    // ---------------- Nectar servo ----------------

    public void openNectar() {
        nectarServo.setPosition(RobotConstants.NECTAR_SERVO_CLOSED_POSITION);
        nectarOpen = true;
    }

    public void closeNectar() {
        nectarServo.setPosition(RobotConstants.NECTAR_SERVO_OPEN_POSITION);
        nectarOpen = false;
    }

    /**
     * Open the nectar gate if it is closed, or close it if it is open
     */
    public void toggleNectar() {
        if (nectarOpen) {
            closeNectar();
        } else {
            openNectar();
        }
    }

    public boolean isNectarOpen() {
        return nectarOpen;
    }

    /**
     * Last commanded nectar servo position (servos do not report their actual position)
     */
    public double getNectarPosition() {
        return nectarServo.getPosition();
    }
}
