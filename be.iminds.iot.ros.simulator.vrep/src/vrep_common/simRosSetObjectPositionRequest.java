package vrep_common;

public interface simRosSetObjectPositionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectPositionRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* position)\n#\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Point position\n";
  int getHandle();
  void setHandle(int value);
  int getRelativeToObjectHandle();
  void setRelativeToObjectHandle(int value);
  geometry_msgs.Point getPosition();
  void setPosition(geometry_msgs.Point value);
}
