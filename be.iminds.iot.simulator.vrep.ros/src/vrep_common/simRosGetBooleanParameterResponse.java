package vrep_common;

public interface simRosGetBooleanParameterResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetBooleanParameterResponse";
  static final java.lang.String _DEFINITION = "int32 parameterValue";
  int getParameterValue();
  void setParameterValue(int value);
}
