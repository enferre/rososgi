package vrep_common;

public interface simRosGetStringParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetStringParameter";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetStringParameter(simInt parameter)\n#\n\nint32 parameter\n---\nint32 result\nstring parameterValue\n";
}
