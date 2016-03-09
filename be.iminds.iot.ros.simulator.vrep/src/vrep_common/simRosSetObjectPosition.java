package vrep_common;

public interface simRosSetObjectPosition extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectPosition";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* position)\n#\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Point position\n---\nint32 result\n";
}
