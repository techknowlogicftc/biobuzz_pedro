package org.firstinspires.ftc.teamcode.opmodes.auton;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.pedropathing.utils.Angle;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous
public class CurveAuto extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Path speed as a fraction of the robot's max achievable velocity (1.0 = full speed).
    // Lower this to move slower.
    private static final double PATH_SPEED = 0.5;

    // Poses
    private final Pose start = poseFactory.of(57, 9, 90);
    private final Pose middle = poseFactory.of(24, 48, 135);
    private final Pose end = poseFactory.of(12, 96, 90);


    private Path park(){
        // through() builds only the curve - every Pedro 3 path also needs a heading, or follow() throws.
        // First half turns start -> middle heading, second half turns middle -> end heading.
        return Paths.through(start, middle, end)
                .heading(Interpolator.piecewise()
                        .until(0.5, Interpolator.linear(start, middle))
                        .until(1.0, Interpolator.linear(middle, end)))
                .with(Constants.foresightConfig.maxPathSpeed.at(PATH_SPEED));
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, park())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        // add your other methods needed in the loop here

        telemetry.addData("X", follower.pose().x());
        telemetry.addData("Y", follower.pose().y());
        telemetry.addData("Heading", Math.toDegrees(follower.pose().heading()));
        telemetry.addData("Follower Mode", follower.mode());
        telemetry.update();
    }
}