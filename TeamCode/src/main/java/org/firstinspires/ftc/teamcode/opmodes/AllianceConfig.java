package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.math.Pose;

/**
 * Configuration class that holds alliance-specific constants for both TeleOp and Autonomous modes.
 * This allows the same base classes to work for both Blue and Red alliances
 * by simply providing different configuration objects.
 *
 * Now uses LaunchTarget enum to manage poses and launcher speeds consistently.
 */
public class AllianceConfig {
    private final boolean isBlueAlliance;
    private final String targetAprilTagIds;
    private final String allianceName;

    /**
     * Constructor for alliance configuration
     * @param isBlueAlliance true for Blue alliance, false for Red alliance
     * @param targetAprilTagIds AprilTag ID to track for relocalization
     * @param allianceName Name of the alliance (for display purposes)
     */
    private AllianceConfig(boolean isBlueAlliance, String targetAprilTagIds, String allianceName) {
        this.isBlueAlliance = isBlueAlliance;
        this.targetAprilTagIds = targetAprilTagIds;
        this.allianceName = allianceName;
    }

    // Getters that use LaunchTarget enum
    public Pose getAllianceHomePose() {
        return LaunchTarget.ALLIANCE_HOME.getPose(isBlueAlliance);
    }

    public Pose getInitPose() {
        return LaunchTarget.INIT_POSE.getPose(isBlueAlliance);
    }

    public Pose getLaunchPose() {
        return LaunchTarget.NEAR_LAUNCH.getPose(isBlueAlliance);
    }

    public Pose getLaunchPoseFar() {
        return LaunchTarget.FAR_LAUNCH.getPose(isBlueAlliance);
    }

    public Pose getAutonEndPose() {
        return LaunchTarget.AUTON_END.getPose(isBlueAlliance);
    }

    public Pose getAutonDoNothingEndPose() {
        return LaunchTarget.AUTON_DO_NOTHING_END.getPose(isBlueAlliance);
    }

    // New method to get pose for any target
    public Pose getTargetPose(LaunchTarget target) {
        return target.getPose(isBlueAlliance);
    }

    // New method to get launcher speed for any target
    public int getLauncherSpeed(LaunchTarget target) {
        return target.getLauncherSpeed();
    }

    public String getTargetAprilTagId() {
        return targetAprilTagIds;
    }

    public String getAllianceName() {
        return allianceName;
    }

    public boolean isBlueAlliance() {
        return isBlueAlliance;
    }

    /**
     * Creates a Blue Alliance configuration
     */
    public static AllianceConfig createBlueAllianceConfig() {
        return new AllianceConfig(
            true,   // isBlueAlliance
            RobotConstants.BLUE_GOAL_APRILTAGS_AF,     // AprilTag ID
            "Blue"  // Alliance name
        );
    }

    public static AllianceConfig createBlueAllianceConfigSF() {
        return new AllianceConfig(
                true,   // isBlueAlliance
                RobotConstants.BLUE_GOAL_APRILTAGS_SF,     // AprilTag ID
                "Blue"  // Alliance name
        );
    }

    /**
     * Creates a Red Alliance configuration
     */
    public static AllianceConfig createRedAllianceConfig() {
        return new AllianceConfig(
            false,  // isBlueAlliance
            RobotConstants.RED_GOAL_APRILTAGS_AF,     // AprilTag ID
            "Red"   // Alliance name
        );
    }
    public static AllianceConfig createRedAllianceConfigSF() {
        return new AllianceConfig(
                false,  // isBlueAlliance
                RobotConstants.RED_GOAL_APRILTAGS_SF,     // AprilTag ID
                "Red"   // Alliance name
        );
    }
}
