package vrep_common;

public interface simRosGetStringParameterResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetStringParameterResponse";
  static final java.lang.String _DEFINITION = "int32 result\nstring parameterValue";
  int getResult();
  void setResult(int value);
  java.lang.String getParameterValue();
  void setParameterValue(java.lang.String value);
}
