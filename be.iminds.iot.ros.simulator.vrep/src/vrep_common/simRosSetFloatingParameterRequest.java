package vrep_common;

public interface simRosSetFloatingParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetFloatingParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetFloatingParameter(simInt parameter,simFloat floatState)\n#\n\nint32 parameter\nfloat32 parameterValue\n";
  int getParameter();
  void setParameter(int value);
  float getParameterValue();
  void setParameterValue(float value);
}
