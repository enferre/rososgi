package vrep_common;

public interface simRosGetIntegerParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetIntegerParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetIntegerParameter(simInt parameter,simInt* intState)\n#\n\nint32 parameter\n";
  int getParameter();
  void setParameter(int value);
}
