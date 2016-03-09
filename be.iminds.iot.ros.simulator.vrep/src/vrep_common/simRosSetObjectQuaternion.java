package vrep_common;

public interface simRosSetObjectQuaternion extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectQuaternion";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n#\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Quaternion quaternion\n---\nint32 result\n";
}
