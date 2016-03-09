package vrep_common;

public interface simRosGetArrayParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetArrayParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetArrayParameter(simInt parameter,simVoid* parameterValues)\n#\n\nint32 parameter\n";
  int getParameter();
  void setParameter(int value);
}
