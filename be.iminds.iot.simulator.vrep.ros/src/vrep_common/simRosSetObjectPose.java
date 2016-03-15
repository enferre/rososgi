package vrep_common;

public interface simRosSetObjectPose extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectPose";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* pos)\n# simInt simSetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n\nint32 handle\nint32 relativeToObjectHandle\ngeometry_msgs/Pose pose\n---\nint32 result\n";
}
