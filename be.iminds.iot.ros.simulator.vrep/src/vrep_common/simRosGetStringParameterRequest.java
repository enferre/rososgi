package vrep_common;

public interface simRosGetStringParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetStringParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetStringParameter(simInt parameter)\n#\n\nint32 parameter\n";
  int getParameter();
  void setParameter(int value);
}
