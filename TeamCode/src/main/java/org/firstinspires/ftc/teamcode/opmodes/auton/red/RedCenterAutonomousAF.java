package org.firstinspires.ftc.teamcode.opmodes.auton.red;

import com.pedropathing.api.Paths;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.opmodes.AllianceConfig;
import org.firstinspires.ftc.teamcode.opmodes.RobotConstants;
import org.firstinspires.ftc.teamcode.opmodes.auton.BaseLinearAutonomous;
import org.firstinspires.ftc.teamcode.pedro.Constants;

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

    @Override
    protected Path park(){
        // through() builds only the curve - every Pedro 3 path also needs a heading, or follow() throws.
        // First half turns start -> middle heading, second half turns middle -> end heading.
        Pose start = poseFactory.of(9, 63, 0);
        Pose middle = poseFactory.of(24, 48, 90);
        Pose end = poseFactory.of(12, 91, 90);

        return Paths.through(start, middle, end)
                .heading(Interpolator.piecewise()
                        .until(0.5, Interpolator.linear(start, middle))
                        .until(1.0, Interpolator.linear(middle, end)))
                .with(Constants.foresightConfig.maxPathSpeed.at(RobotConstants.TEST_PATH_SPEED));
    }
}
