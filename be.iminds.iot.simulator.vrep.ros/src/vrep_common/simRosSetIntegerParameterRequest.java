package vrep_common;

public interface simRosSetIntegerParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetIntegerParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetIntegerParameter(simInt parameter,simInt intState)\n#\n\nint32 parameter\nint32 parameterValue\n";
  int getParameter();
  void setParameter(int value);
  int getParameterValue();
  void setParameterValue(int value);
}
