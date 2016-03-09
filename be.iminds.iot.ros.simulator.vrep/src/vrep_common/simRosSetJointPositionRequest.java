package vrep_common;

public interface simRosSetJointPositionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointPositionRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetJointPosition(simInt objectHandle,simFloat position)\n#\n\nint32 handle\nfloat64 position\n";
  int getHandle();
  void setHandle(int value);
  double getPosition();
  void setPosition(double value);
}
