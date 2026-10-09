package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.math.Pose;

public class PoseHolder {

    private static Pose currentPose;

    public static void setCurrentPose(Pose pose) {
        currentPose = pose;
    }

    public static Pose getCurrentPose() {
        return currentPose;
    }

}
