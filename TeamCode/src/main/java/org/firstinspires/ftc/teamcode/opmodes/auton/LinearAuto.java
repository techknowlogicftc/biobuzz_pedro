package org.firstinspires.ftc.teamcode.opmodes.auton;

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
public class LinearAuto extends OpMode {
    private Follower follower;
    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Path speed as a fraction of the robot's max achievable velocity (1.0 = full speed).
    // Lower this to move slower.
    private static final double PATH_SPEED = 0.5;

    // Poses
    private final Pose startPose = poseFactory.of(9, 9, 0);
    private final Pose scorePose = poseFactory.of(57, 9, 0);
    private final Pose parkPose = poseFactory.of(10, 10, 180);

    private final Pose parkPose2 = poseFactory.of(10, 72, 90);

    // Path methods
    private Path startToScore() {
        return line(startPose, scorePose).linear(startPose, scorePose)
                .with(Constants.foresightConfig.maxPathSpeed.at(PATH_SPEED));
    }

    private Path park(){
        // 0 -> 180 is a half turn, so linear() could go either way; force counterclockwise
        return line(scorePose, parkPose).heading(counterClockwise(scorePose, parkPose))
                .with(Constants.foresightConfig.maxPathSpeed.at(PATH_SPEED));
    }

    private Path finalPark(){
        // 0 -> 180 is a half turn, so linear() could go either way; force counterclockwise
        return line(parkPose, parkPose2).linear(parkPose, parkPose2)
                .with(Constants.foresightConfig.maxPathSpeed.at(PATH_SPEED));
    }

    /**
     * Heading that always turns counterclockwise (left) from start to end over the path.
     */
    private static Interpolator counterClockwise(Pose start, Pose end) {
        double startHeading = start.heading();
        double turn = Angle.normalize(end.heading() - startHeading);  // counterclockwise amount, 0..2π
        return (curve, t) -> Angle.normalize(startHeading + turn * curve.pathCompletion(t));
    }

    private Command autoRoutine() {
        return sequential(
                follow(follower, startToScore()),
                // Add mechanism commands here.
                follow(follower, park()),

                follow(follower, finalPark())
        );
    }

    @Override
    public void init() {
        Scheduler.reset();

        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
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