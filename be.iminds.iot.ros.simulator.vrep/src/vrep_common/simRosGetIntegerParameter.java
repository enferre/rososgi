package vrep_common;

public interface simRosGetIntegerParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetIntegerParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetIntegerParameter(simInt parameter,simInt* intState)\n#\n\nint32 parameter\n---\nint32 result\nint32 parameterValue\n";
}
