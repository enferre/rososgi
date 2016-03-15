package vrep_common;

public interface simRosGetJointStateRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointStateRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetJointPosition(simInt objectHandle,simFloat* position)\n# ...\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
