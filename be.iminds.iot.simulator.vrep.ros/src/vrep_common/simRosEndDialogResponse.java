package vrep_common;

public interface simRosEndDialogResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEndDialogResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
