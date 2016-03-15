package vrep_common;

public interface simRosGetObjectIntParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectIntParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectIntParameter(simInt objectHandle,simInt parameterID,simInt* parameter)\n#\n\nint32 handle\nint32 parameterID\n---\nint32 result\nint32 parameterValue\n";
}
