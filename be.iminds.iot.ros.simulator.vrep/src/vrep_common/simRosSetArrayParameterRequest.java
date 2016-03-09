package vrep_common;

public interface simRosSetArrayParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetArrayParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetArrayParameter(simInt parameter,simVoid* parameterValues)\n#\n\nint32 parameter\nfloat32[] parameterValues\n";
  int getParameter();
  void setParameter(int value);
  float[] getParameterValues();
  void setParameterValues(float[] value);
}
