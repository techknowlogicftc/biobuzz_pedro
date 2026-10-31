package org.firstinspires.ftc.teamcode.opmodes.auton.blue;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.auton.BaseLinearAutonomous;

import java.util.ArrayList;
import java.util.List;

/**
 * Blue Alliance center autonomous (SF):
 * 1. Spin up the pollen launcher, open the pollen gate and fire, then stop it
 * 2. Spin up the nectar launcher, open the nectar gate and fire, then stop it
 * 3. Close both gates
 * 4. Return to auton end position (AUTON_END_SF)
 *
 * Alliance-specific poses come from AllianceConfig / LaunchTarget; the sequence itself is shared logic.
 */
@Autonomous(name = "Blue-Center-SF", group = "Blue-Center-SF")
public class BlueCenterAutonomousSF extends BaseLinearAutonomous {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createBlueAllianceConfigSF();
    }

    @Override
    protected List<StateNode> buildStateSequence() {
        List<StateNode> sequence = new ArrayList<>();

        // Start launcher motors
        sequence.add(state(AutonomousState.START_POLLEN_LAUNCHER));
        sequence.add(waitMs(500));
        sequence.add(state(AutonomousState.OPEN_POLLEN_SERVO));
        sequence.add(waitMs(5000));
        sequence.add(state(AutonomousState.STOP_POLLEN_LAUNCHER));
        sequence.add(state(AutonomousState.START_NECTAR_LAUNCHER));
        sequence.add(waitMs(500));
        //open the gates
        sequence.add(state(AutonomousState.OPEN_NECTAR_SERVO));
        sequence.add(waitMs(3000));
        // Stop launchers
        sequence.add(state(AutonomousState.STOP_NECTAR_LAUNCHER));
        //close gate
        sequence.add(state(AutonomousState.CLOSE_POLLEN_SERVO));
        sequence.add(state(AutonomousState.CLOSE_NECTAR_SERVO));
        sequence.add(waitMs(1000));
        // Return to auton end position
        sequence.add(state(AutonomousState.RETURN_HOME));
        return sequence;
    }
}
