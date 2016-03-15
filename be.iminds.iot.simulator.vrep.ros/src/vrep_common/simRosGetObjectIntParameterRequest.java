package vrep_common;

public interface simRosGetObjectIntParameterRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectIntParameterRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectIntParameter(simInt objectHandle,simInt parameterID,simInt* parameter)\n#\n\nint32 handle\nint32 parameterID\n";
  int getHandle();
  void setHandle(int value);
  int getParameterID();
  void setParameterID(int value);
}
