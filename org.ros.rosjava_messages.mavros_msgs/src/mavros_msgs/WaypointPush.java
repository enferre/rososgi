package mavros_msgs;

public interface WaypointPush extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/WaypointPush";
  static final java.lang.String _DEFINITION = "# Send waypoints to device\n#\n# Returns success status and transfered count\n\nmavros_msgs/Waypoint[] waypoints\n---\nbool success\nuint32 wp_transfered\n";
}
