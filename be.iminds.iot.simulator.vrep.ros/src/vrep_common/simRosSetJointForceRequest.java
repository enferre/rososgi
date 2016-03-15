package vrep_common;

public interface simRosSetJointForceRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointForceRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointForce(simInt objectHandle,simFloat forceOrTorque)\n#\n\nint32 handle\nfloat64 forceOrTorque\n";
  int getHandle();
  void setHandle(int value);
  double getForceOrTorque();
  void setForceOrTorque(double value);
}
