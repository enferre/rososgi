package vrep_common;

public interface simRosSetJointPosition extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointPosition";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointPosition(simInt objectHandle,simFloat position)\n#\n\nint32 handle\nfloat64 position\n---\nint32 result\n";
}
