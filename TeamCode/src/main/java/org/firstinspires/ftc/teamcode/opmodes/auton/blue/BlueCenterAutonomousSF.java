package org.firstinspires.ftc.teamcode.opmodes.auton.blue;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;
import org.firstinspires.ftc.teamcode.opmodes.auton.BaseLinearAutonomous;
import org.firstinspires.ftc.teamcode.pedro.Constants;

import java.util.ArrayList;
import java.util.List;
import com.pedropathing.paths.Path;
/**
 * Blue Alliance center autonomous (SF):
 * 1. Spin up the pollen launcher, open the pollen gate and fire, then stop it
 * 2. Spin up the nectar launcher, open the nectar gate and fire, then stop it
 * 3. Close both gates
 * 4. Return to auton end position (AUTON_END_SF)
 *
 * Alliance-specific poses come from AllianceConfig / LaunchTarget; the sequence itself is shared logic.
 */
@Autonomous(name = "Blue-Center-AF", group = "Blue-Center-AF")
public class BlueCenterAutonomousSF extends BaseLinearAutonomous {

    @Override
    protected AllianceConfig getAllianceConfig() {
        return AllianceConfig.createBlueAllianceConfigSF();
    }

    @Override
    protected List<StateNode> buildStateSequence() {
        List<StateNode> sequence = new ArrayList<>();

        sequence.add(waitMs(20000));
        // Return to auton end position
        sequence.add(state(AutonomousState.RETURN_HOME));
        return sequence;
    }

    @Override
    protected Path park() {
        Pose start = follower.pose();
        Pose end = allianceConfig.getAutonEndPose();
        double startHrading = Math.toDegrees(start.heading());
        // Push the control points away from the nearest side wall (x = 0 for Red, x = 144 for Blue)
        Pose control1 = new Pose(start.x() , start.y()+10, 0);  // Bezier control points; their headings are unused

        return Paths.curve(start, control1, end)
                .linear(start, end)
                .with(Constants.foresightConfig.maxPathSpeed.at(RobotConstants.TEST_PATH_SPEED));
    }
}
