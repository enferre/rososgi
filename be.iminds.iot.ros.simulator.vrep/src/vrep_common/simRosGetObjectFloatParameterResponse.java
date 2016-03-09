package vrep_common;

public interface simRosGetObjectFloatParameterResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectFloatParameterResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32 parameterValue";
  int getResult();
  void setResult(int value);
  float getParameterValue();
  void setParameterValue(float value);
}
