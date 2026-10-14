package org.firstinspires.ftc.teamcode.opmodes.teleop;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;

/**
 * Red Alliance TeleOp - all control logic lives in BaseLinearTeleOp.
 */
@TeleOp(name = "Red TeleOp", group = "Red")
public class RedAllianceLinearTeleOp extends BaseLinearTeleOp {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createRedAllianceConfig();
    }
}
