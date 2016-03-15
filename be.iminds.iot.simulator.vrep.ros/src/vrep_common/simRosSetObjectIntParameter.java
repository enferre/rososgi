package vrep_common;

public interface simRosSetObjectIntParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectIntParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectIntParameter(simInt objectHandle,simInt parameterID,simInt parameter)\n#\n\nint32 handle\nint32 parameter\nint32 parameterValue\n---\nint32 result\n";
}
