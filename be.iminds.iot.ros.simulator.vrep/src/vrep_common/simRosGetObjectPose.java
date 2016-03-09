package vrep_common;

public interface simRosGetObjectPose extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectPose";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* position)\n# simInt simGetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n\nint32 handle\nint32 relativeToObjectHandle\n---\nint32 result\ngeometry_msgs/PoseStamped pose\n";
}
