package org.firstinspires.ftc.teamcode.opmodes.auton.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.auton.BaseLinearAutonomous;

import java.util.ArrayList;
import java.util.List;

/**
 * Red Alliance center autonomous:
 * 1. Move to near launch position and pause (add mechanism states here)
 * 2. PPG collection cycle (move to intake start, slow move to intake end)
 * 3. Return to auton end position
 *
 * Alliance-specific poses come from AllianceConfig / LaunchTarget; the sequence itself is shared logic.
 */
@Autonomous(name = "Red-Center-AF", group = "Red-Center-AF")
public class RedCenterAutonomousAF extends BaseLinearAutonomous {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createRedAllianceConfigAF();
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
        sequence.add(waitMs(500));
        sequence.add(state(AutonomousState.CLOSE_POLLEN_SERVO));
        // Return to auton end position
        sequence.add(state(AutonomousState.RETURN_HOME));
        return sequence;
    }
}
