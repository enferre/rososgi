package vrep_common;

public interface simRosSetJointTargetPositionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointTargetPositionRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointTargetPosition(simInt objectHandle,simFloat targetPosition)\n#\n\nint32 handle\nfloat64 targetPosition\n";
  int getHandle();
  void setHandle(int value);
  double getTargetPosition();
  void setTargetPosition(double value);
}
