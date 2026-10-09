package org.firstinspires.ftc.teamcode.opmodes.auton.red;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.LaunchTarget;
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
@Autonomous(name = "Red-center", group = "Red-center")
public class Red_Center_Autonomous extends BaseLinearAutonomous {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createRedAllianceConfig();
    }

    @Override
    protected List<StateNode> buildStateSequence() {
        List<StateNode> sequence = new ArrayList<>();

        // Navigate to launch position
        sequence.add(moveTo(LaunchTarget.NEAR_LAUNCH));
        sequence.add(waitMs(500));

        // PPG collection cycle
        addCollectionCycle(sequence, LaunchTarget.PPG_INTAKE_START, LaunchTarget.PPG_INTAKE_END);

        // Return to auton end position
        sequence.add(state(AutonomousState.RETURN_HOME));

        return sequence;
    }
}
