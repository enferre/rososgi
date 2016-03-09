package vrep_common;

public interface simRosSetObjectQuaternionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectQuaternionRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n#\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Quaternion quaternion\n";
  int getHandle();
  void setHandle(int value);
  int getRelativeToObjectHandle();
  void setRelativeToObjectHandle(int value);
  geometry_msgs.Quaternion getQuaternion();
  void setQuaternion(geometry_msgs.Quaternion value);
}
