package org.firstinspires.ftc.teamcode.opmodes;

import com.pedropathing.math.Pose;

/**
 * LaunchTarget - Enum defining all possible robot target positions with associated launcher speeds
 *
 * Each target has:
 * - Blue alliance pose
 * - Red alliance pose
 * - Launcher speed (in ticks per second)
 *
 * This makes it easy to add new targets and ensures launcher speeds are consistent
 * across autonomous and teleop modes.
 */
public enum LaunchTarget {
    // Launch positions
    NEAR_LAUNCH(
        new Pose(8, 13.1, Math.toRadians(220)),          // Blue alliance pose
        new Pose(3.5, -20.9, Math.toRadians(135)),     // Red alliance pose
            RobotConstants.NEAR_LAUNCH_SPEED                                             // Launcher speed
    ),

    FAR_LAUNCH(
        new Pose(20, -20, Math.toRadians(240)),         // Blue alliance pose
        new Pose(5, 2, Math.toRadians(133)),            // Red alliance pose
            RobotConstants.NEAR_LAUNCH_SPEED                                             // Launcher speed
    ),

    OBELISK_LAUNCH(
            new Pose(57.9, -4, Math.toRadians(270)),         // Blue alliance pose
            new Pose(51.4, -20, Math.toRadians(90)),            // Red alliance pose
            1900                                             // Launcher speed
    ),

    SMALL_TRIANGLE_AUTON_LAUNCH(
            new Pose(-58.6, 9.9, Math.toRadians(200)),         // Blue alliance pose
            new Pose(-60.3, -20.5, Math.toRadians(156.0)),            // Red alliance pose
            RobotConstants.FAR_LAUNCH_SPEED
    ),

    // Add more launch positions here as needed for additional gamepad buttons
    // Example:
    // MID_LAUNCH(
    //     new Pose(15, 0, Math.toRadians(230)),        // Blue alliance pose
    //     new Pose(8, -10, Math.toRadians(140)),       // Red alliance pose
    //     1900                                          // Launcher speed
    // ),

    // Autonomous end positions (no launcher)
    AUTON_END(
        new Pose(-20, 12, Math.toRadians(220)),          // Blue alliance pose
        new Pose(-20, -24, Math.toRadians(135)),        // Red alliance pose
        0                                                // No launcher
    ),

    AUTON_END_CENTER(
            new Pose(-24, 26, Math.toRadians(90)),          // Blue alliance pose
            new Pose(-24, -26, Math.toRadians(270)),        // Red alliance pose
            0                                                // No launcher
    ),

    AUTON_DO_NOTHING_END(
        new Pose(-58.6, 22, Math.toRadians(0)),           // Blue alliance pose
        new Pose(-60, -32, Math.toRadians(156)),          // Red alliance pose
        0                                                // No launcher
    ),

    // Home/starting positions
    ALLIANCE_HOME(
        new Pose(-52, -32, 0),                          // Blue alliance pose
        new Pose(-48, 34, 0),                           // Red alliance pose
        0                                                // No launcher
    ),

    INIT_POSE(
        new Pose(-67.2, 50.4, Math.toRadians(180)),         // Blue alliance init position
        new Pose(-66.3, -61.6, Math.toRadians(180)),          // Red alliance init position

        0                                                // No launcher
    ),

    // Ball collection positions for autonomous - GPP (first intake cycle)
    GPP_INTAKE_START(
        new Pose(-52, 18, Math.toRadians(90)),           // Blue alliance intake start position
        new Pose(-44, -27.7, Math.toRadians(270)),     // Red alliance intake start position
        0                                                // No launcher
    ),

    GPP_INTAKE_END(
        new Pose(-52, 59, Math.toRadians(90)),           // Blue alliance intake end position (slow movement)
        new Pose(-44, -59, Math.toRadians(270)),         // Red alliance intake end position (slow movement)
        0                                                // No launcher
    ),

    // Ball collection positions for autonomous - PGP (second intake cycle, 24" less on X)
    PGP_INTAKE_START(
        new Pose(-26, 18, Math.toRadians(90)),           // Blue alliance intake start position
        new Pose(-18, -27.7, Math.toRadians(270)),     // Red alliance intake start position
        0                                                // No launcher
    ),

    PGP_INTAKE_END(
        new Pose(-26, 59, Math.toRadians(90)),           // Blue alliance intake end position (slow movement)
        new Pose(-18, -59, Math.toRadians(270)),         // Red alliance intake end position (slow movement)
        0                                                // No launcher
    ),

    // Ball collection positions for autonomous - PPG (third intake cycle, 48" less on X from GPP)
    PPG_INTAKE_START(
        new Pose(0, 18, Math.toRadians(90)),           // Blue alliance intake start position
        new Pose(6, -27.7, Math.toRadians(270)),     // Red alliance intake start position
        0                                                // No launcher
    ),

    PPG_INTAKE_END(
        new Pose(0, 52, Math.toRadians(90)),           // Blue alliance intake end position (slow movement)
        new Pose(6, -52, Math.toRadians(270)),         // Red alliance intake end position (slow movement)
        0                                                // No launcher
    );

    private final Pose bluePose;
    private final Pose redPose;
    private final int launcherSpeed;

    /**
     * Constructor for LaunchTarget
     * @param bluePose Pose for Blue alliance
     * @param redPose Pose for Red alliance
     * @param launcherSpeed Launcher speed in ticks per second (0 for non-launch positions)
     */
    LaunchTarget(Pose bluePose, Pose redPose, int launcherSpeed) {
        this.bluePose = bluePose;
        this.redPose = redPose;
        this.launcherSpeed = launcherSpeed;
    }

    /**
     * Get the pose for the specified alliance
     * @param isBlueAlliance true for Blue alliance, false for Red alliance
     * @return The alliance-specific pose
     */
    public Pose getPose(boolean isBlueAlliance) {
        return isBlueAlliance ? bluePose : redPose;
    }

    /**
     * Get the launcher speed for this target
     * @return Launcher speed in ticks per second
     */
    public int getLauncherSpeed() {
        return launcherSpeed;
    }

    /**
     * Check if this target requires the launcher
     * @return true if launcher speed > 0
     */
    public boolean requiresLauncher() {
        return launcherSpeed > 0;
    }
}
