package vrep_common;

public interface simRosGetFloatSignalResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetFloatSignalResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32 signalValue";
  int getResult();
  void setResult(int value);
  float getSignalValue();
  void setSignalValue(float value);
}
