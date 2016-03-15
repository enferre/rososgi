package vrep_common;

public interface simRosSetJointTargetVelocity extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointTargetVelocity";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointTargetVelocity(simInt objectHandle,simFloat targetVelocity)\n#\n\nint32 handle\nfloat64 targetVelocity\n---\nint32 result\n";
}
