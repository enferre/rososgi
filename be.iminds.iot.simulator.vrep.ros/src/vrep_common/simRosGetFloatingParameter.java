package vrep_common;

public interface simRosGetFloatingParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetFloatingParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetFloatingParameter(simInt parameter,simFloat* floatState)\n#\n\nint32 parameter\n---\nint32 result\nfloat32 parameterValue\n";
}
