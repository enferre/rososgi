package vrep_common;

public interface simRosGetDialogInputResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetDialogInputResponse";
  static final java.lang.String _DEFINITION = "int32 result\nstring input";
  int getResult();
  void setResult(int value);
  java.lang.String getInput();
  void setInput(java.lang.String value);
}
