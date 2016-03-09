package vrep_common;

public interface simRosSetObjectPoseRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectPoseRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* pos)\n# simInt simSetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Pose pose\n";
  int getHandle();
  void setHandle(int value);
  int getRelativeToObjectHandle();
  void setRelativeToObjectHandle(int value);
  geometry_msgs.Pose getPose();
  void setPose(geometry_msgs.Pose value);
}
