package vrep_common;

public interface simRosSetBooleanParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetBooleanParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetBooleanParameter(simInt parameter,simBool boolState)\n#\n\nint32 parameter\nuint8 parameterValue\n";
  int getParameter();
  void setParameter(int value);
  byte getParameterValue();
  void setParameterValue(byte value);
}
