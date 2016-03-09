package vrep_common;

public interface simRosSetIntegerParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetIntegerParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetIntegerParameter(simInt parameter,simInt intState)\n#\n\nint32 parameter\nint32 parameterValue\n---\nint32 result\n";
}
