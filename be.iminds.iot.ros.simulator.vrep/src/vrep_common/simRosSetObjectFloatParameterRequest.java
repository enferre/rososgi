package vrep_common;

public interface simRosSetObjectFloatParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectFloatParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectFloatParameter(simInt objectHandle,simInt parameterID,simFloat parameter)\n#\n\nint32 handle\nint32 parameter\nfloat32 parameterValue\n";
  int getHandle();
  void setHandle(int value);
  int getParameter();
  void setParameter(int value);
  float getParameterValue();
  void setParameterValue(float value);
}
