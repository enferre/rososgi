package vrep_common;

public interface simRosGetObjectPoseRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectPoseRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectPosition(simInt objectHandle,simInt relativeToObjectHandle,simFloat* position)\n# simInt simGetObjectQuaternion(simInt objectHandle,simInt relativeToObjectHandle,simFloat* quat)\n\nint32 handle\nint32 relativeToObjectHandle\n";
  int getHandle();
  void setHandle(int value);
  int getRelativeToObjectHandle();
  void setRelativeToObjectHandle(int value);
}
