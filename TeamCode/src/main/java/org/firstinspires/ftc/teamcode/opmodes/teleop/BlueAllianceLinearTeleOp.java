package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;

/**
 * Blue Alliance TeleOp - all control logic lives in BaseLinearTeleOp.
 */
@TeleOp(name = "Blue TeleOp", group = "Blue")
public class BlueAllianceLinearTeleOp extends BaseLinearTeleOp {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createBlueAllianceConfig();
    }
}
