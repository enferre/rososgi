package vrep_common;

public interface simRosSetObjectIntParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectIntParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectIntParameter(simInt objectHandle,simInt parameterID,simInt parameter)\n#\n\nint32 handle\nint32 parameter\nint32 parameterValue\n";
  int getHandle();
  void setHandle(int value);
  int getParameter();
  void setParameter(int value);
  int getParameterValue();
  void setParameterValue(int value);
}
