package vrep_common;

public interface simRosGetObjectFloatParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectFloatParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectFloatParameter(simInt objectHandle,simInt parameterID,simFloat* parameter)\n#\n\nint32 handle\nint32 parameterID\n---\nint32 result\nfloat32 parameterValue\n";
}
