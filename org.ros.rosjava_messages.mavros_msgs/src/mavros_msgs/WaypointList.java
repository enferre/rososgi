package mavros_msgs;

public interface WaypointList extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/WaypointList";
  static final java.lang.String _DEFINITION = "mavros_msgs/Waypoint[] waypoints\n";
  java.util.List<mavros_msgs.Waypoint> getWaypoints();
  void setWaypoints(java.util.List<mavros_msgs.Waypoint> value);
}
