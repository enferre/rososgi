package vrep_common;

public interface simRosSetObjectFloatParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectFloatParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectFloatParameter(simInt objectHandle,simInt parameterID,simFloat parameter)\n#\n\nint32 handle\nint32 parameter\nfloat32 parameterValue\n---\nint32 result\n";
}
