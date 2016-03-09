package vrep_common;

public interface simRosSetJointForce extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointForce";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointForce(simInt objectHandle,simFloat forceOrTorque)\n#\n\nint32 handle\nfloat64 forceOrTorque\n---\nint32 result\n";
}
