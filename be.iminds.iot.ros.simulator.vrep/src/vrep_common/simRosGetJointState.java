package vrep_common;

public interface simRosGetJointState extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointState";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetJointPosition(simInt objectHandle,simFloat* position)\n# ...\n\nint32 handle\n---\nint32 result\nsensor_msgs/JointState state\n";
}
