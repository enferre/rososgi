package vrep_common;

public interface simRosSetJointTargetPosition extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointTargetPosition";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointTargetPosition(simInt objectHandle,simFloat targetPosition)\n#\n\nint32 handle\nfloat64 targetPosition\n---\nint32 result\n";
}
