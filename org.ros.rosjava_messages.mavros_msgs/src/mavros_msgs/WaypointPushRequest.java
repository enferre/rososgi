package mavros_msgs;

public interface WaypointPushRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/WaypointPushRequest";
  static final java.lang.String _DEFINITION = "# Send waypoints to device\n#\n# Returns success status and transfered count\n\nmavros_msgs/Waypoint[] waypoints\n";
  java.util.List<mavros_msgs.Waypoint> getWaypoints();
  void setWaypoints(java.util.List<mavros_msgs.Waypoint> value);
}
