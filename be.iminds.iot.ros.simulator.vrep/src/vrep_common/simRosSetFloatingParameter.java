package vrep_common;

public interface simRosSetFloatingParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetFloatingParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetFloatingParameter(simInt parameter,simFloat floatState)\n#\n\nint32 parameter\nfloat32 parameterValue\n---\nint32 result\n";
}
