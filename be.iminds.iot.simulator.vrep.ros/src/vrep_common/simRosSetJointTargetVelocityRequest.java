package vrep_common;

public interface simRosSetJointTargetVelocityRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointTargetVelocityRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointTargetVelocity(simInt objectHandle,simFloat targetVelocity)\n#\n\nint32 handle\nfloat64 targetVelocity\n";
  int getHandle();
  void setHandle(int value);
  double getTargetVelocity();
  void setTargetVelocity(double value);
}
