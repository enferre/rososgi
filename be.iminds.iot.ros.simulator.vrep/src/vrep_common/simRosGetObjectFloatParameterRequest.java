package vrep_common;

public interface simRosGetObjectFloatParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectFloatParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectFloatParameter(simInt objectHandle,simInt parameterID,simFloat* parameter)\n#\n\nint32 handle\nint32 parameterID\n";
  int getHandle();
  void setHandle(int value);
  int getParameterID();
  void setParameterID(int value);
}
