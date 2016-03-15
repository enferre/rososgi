package vrep_common;

public interface simRosGetFloatingParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetFloatingParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetFloatingParameter(simInt parameter,simFloat* floatState)\n#\n\nint32 parameter\n";
  int getParameter();
  void setParameter(int value);
}
