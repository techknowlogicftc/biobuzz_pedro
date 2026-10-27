package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {

    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("leftFront");
        c.frontRightName.set("rightFront");
        c.backLeftName.set("leftBack");
        c.backRightName.set("rightBack");
        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
        // false = FLOAT (coast) at zero power in manual drive and stop(); path following always uses FLOAT
        c.manualBrakeMode.set(false);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(3.5965419378806285);
        c.yPodOffset.set(6.333354439322405);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.30648630110039154);
                Controller secondaryTranslationalForward = Controller.proportional(0.11323849879180174);
                Controller primaryTranslationalLateral = Controller.proportional(0.3778303678529677);
                Controller secondaryTranslationalLateral = Controller.proportional(0.13959822510830525);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.01829157749474892));
                c.brake.set(Controller.proportionalFeedforward(0.01554784087053658));

                c.headingFeedback.set(Controller.proportional(6.19677387771864));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.03971302655708713, 0.005050479038029129));

                c.linearBrakeCoefficients.set(Matrix.diag(0.08209155914187458, 0.0566857300692305));
                c.quadraticBrakeCoefficients.set(Matrix.diag(7.452345740362047E-4, 0.0012293432750717784));

                c.maxAchievableForwardVelocity.set(53.87872229644436);
                c.maxAchievableStrafeVelocity.set(41.60852427904258);
                c.naturalForwardDeceleration.set(28.857851349187985);
                c.naturalStrafeDeceleration.set(67.17923388855024);
            }
    );
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }


}