package vrep_common;

public interface simRosGetBooleanParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetBooleanParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetBooleanParameter(simInt parameter)\n#\n\nint32 parameter\n";
  int getParameter();
  void setParameter(int value);
}
