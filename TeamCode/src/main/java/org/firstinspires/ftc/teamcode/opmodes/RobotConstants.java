package org.firstinspires.ftc.teamcode.opmodes;

/**
 * RobotConstants - Shared constants used across autonomous and teleop programs
 * 
 * This class contains all the common constants used by both BaseAutonomous and BaseMultiPlayerTeleOp
 * to avoid duplication and ensure consistency across the codebase.
 */
public class RobotConstants {
    
    // Position and heading tolerance constants
    public static final double POSITION_TOLERANCE = 3.0;  // 3mm tolerance for position checking (tighter for accuracy)
    public static final double HEADING_TOLERANCE = Math.toRadians(3.0);  // 3 degrees tolerance for heading checking (tighter for accuracy)
    
    // Fine adjustment tolerance constants (for teleop)
    public static final double FINE_POSITION_TOLERANCE = 2.0;  // 2mm tolerance for fine position adjustment (even tighter)
    public static final double FINE_HEADING_TOLERANCE = Math.toRadians(2.0);  // 2 degrees tolerance for fine heading adjustment (even tighter)
    public static final double FINE_ADJUSTMENT_SPEED = 0.3;  // Speed multiplier for fine adjustment movements
    public static final long FINE_ADJUSTMENT_TIMEOUT_MS = 5000;  // 5 second timeout for fine adjustment
    
    // Transition constants (for teleop)
    public static final long TRANSITION_DURATION_MS = 1000;  // 1 second smooth transition period
    
    // Feature toggle flags (for teleop)
    public static final boolean ENABLE_SMOOTH_TRANSITION = true;  // Enable/disable 1-second transition
    public static final boolean ENABLE_CAMERA_FINE_ADJUSTMENT = false;  // Enable/disable camera fine adjustment
    public static final boolean ENABLE_CAMERA_RELOCALIZATION = false;  // Enable/disable continuous camera relocalization

    // Camera relocalization constants (for teleop)
    public static final long CAMERA_RELOCALIZATION_INTERVAL_MS = 500;  // Throttle relocalization updates (2Hz)
    public static final double MAX_RELOCALIZATION_DISTANCE_MM = 600.0;  // Reject relocalization jumps > 600mm
    public static final double MIN_RELOCALIZATION_DISTANCE_MM = 10.0;  // Ignore tiny corrections < 10mm
    public static final long CAMERA_DATA_MAX_STALENESS_MS = 100;  // Reject camera data older than 100ms
    public static final int BLUE_GOAL_APRILTAG_ID = 20;  // Blue alliance goal AprilTag ID
    public static final int RED_GOAL_APRILTAG_ID = 24;  // Red alliance goal AprilTag ID

    // Servo constants
    public static final double SERVO_DELAY_MS = 1000;  // Fallback delay if velocity check fails (1 second to ensure motors reach speed)
    public static final long VELOCITY_CHECK_MIN_DELAY_MS = 600;  // Minimum delay before starting velocity check (motors need time to spin up)
    public static final long SERVO_SETTLING_DELAY_MS = 250;  // Time for servo to physically reach target position (250ms)
    public static final double SERVO_RETRACT_DELAY_MS = 700;  // Total time at push position before retract (700ms = 250ms settling + 450ms pushing)
    public static final double SERVO_PUSH_POSITION = 0.4429;  // Servo position to push ball
    public static final double SERVO_REST_POSITION = 0.4;  // Servo rest position
    public static final double LAUNCHER_VELOCITY_TOLERANCE_LOWER = 0.03;  // 3% tolerance below target for launcher speed check
    public static final double LAUNCHER_VELOCITY_TOLERANCE_UPPER = 0.05;  // 5% tolerance above target for launcher speed check
    
    // Ball kicker servo positions
    public static final double SLOT1_STARTING_POSITION = 0.44;  // Slot 1 kicker rest position
    public static final double SLOT1_ENDING_POSITION = 0.550;     // Slot 1 kicker extended position
    public static final double SLOT2_STARTING_POSITION = 0.46;  // Slot 2 kicker rest position
    public static final double SLOT2_ENDING_POSITION = 0.6;     // Slot 2 kicker extended position
    public static final double SLOT3_STARTING_POSITION = 0.54;     // Slot 3 kicker rest position
    public static final double SLOT3_ENDING_POSITION = 0.3;     // Slot 3 kicker extended position
    public static final double KICKER_DELAY_MS = 500;           // 500ms delay for ball kickers
    public static final long BALL_TRAVEL_DELAY_MS = 800;        // Time for ball to travel from kicker to launcher area
    
    // Launcher velocity constants (ticks per second)
    public static final int NEAR_LAUNCH_SPEED = 2080;         // Full speed for normal launch (teleop)
    public static final int FAR_LAUNCH_SPEED = 2350;         // High speed for small triangle launch (teleop)
    public static final int LAUNCHER_DUMMY_SPEED = 1000;        // Half speed for dummy launch
    public static final int LAUNCHER_STOP_SPEED = 0;            // Stop speed
    
    // Autonomous-specific constants
    public static final boolean ENABLE_BALL_COLLECTION_SEQUENCE = true;  // Enable/disable ball collection after initial launch
    public static final double BALL_COLLECTION_SPEED = 0.25;  // 1/4 speed for ball collection
    public static final double INTAKE_MOVEMENT_SPEED = 0.3;  // 1/2 speed for slow intake movement
    public static final long LAUNCH_DELAY_MS = 2000;  // 2 second delay between launches
    public static final long INTAKE_DURATION_MS = 3000;  // 3 seconds for ball collection
    public static final long APRIL_TAG_TIMEOUT_MS = 5000;  // 5 second timeout for AprilTag detection
    public static final long PATH_TIMEOUT_MS = 4000;  // 4 second timeout for path following (increased for heading settling)
    public static final long INTAKE_PATH_TIMEOUT_MS = 5000;  // 5 second timeout for slow intake movement
    public static final long HEADING_SETTLING_DELAY_MS = 200;  // 200ms delay after path completes to allow heading to settle before launch
    public static final double SHORT_PATH_THRESHOLD_MM = 150.0;  // Paths shorter than this use constant heading, longer paths use heading interpolation
    public static final double AUTON_SPEED_MULTIPLIER = 0.8;
    public static final long AUTONOMOUS_RETURN_HOME_DEADLINE_MS = 28000;  // 28 seconds - Force return home to ensure completion within 30 seconds
}
