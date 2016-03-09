package vrep_common;

public interface simRosGetObjectIntParameterResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectIntParameterResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32 parameterValue";
  int getResult();
  void setResult(int value);
  int getParameterValue();
  void setParameterValue(int value);
}
