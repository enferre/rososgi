package vrep_common;

public interface simRosGetArrayParameterResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetArrayParameterResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32[] parameterValues";
  int getResult();
  void setResult(int value);
  float[] getParameterValues();
  void setParameterValues(float[] value);
}
