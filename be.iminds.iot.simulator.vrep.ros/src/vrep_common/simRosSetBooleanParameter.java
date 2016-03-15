package vrep_common;

public interface simRosSetBooleanParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetBooleanParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetBooleanParameter(simInt parameter,simBool boolState)\n#\n\nint32 parameter\nuint8 parameterValue\n---\nint32 result\n";
}
