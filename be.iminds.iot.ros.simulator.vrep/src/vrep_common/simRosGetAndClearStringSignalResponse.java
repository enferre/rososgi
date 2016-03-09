package vrep_common;

public interface simRosGetAndClearStringSignalResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetAndClearStringSignalResponse";
  static final java.lang.String _DEFINITION = "int32 result\nstring signalValue";
  int getResult();
  void setResult(int value);
  java.lang.String getSignalValue();
  void setSignalValue(java.lang.String value);
}
