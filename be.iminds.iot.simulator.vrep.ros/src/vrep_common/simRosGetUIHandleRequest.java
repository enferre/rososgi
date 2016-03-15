package vrep_common;

public interface simRosGetUIHandleRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIHandleRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIHandle(const simChar* uiName)\n#\n\nstring uiName\n";
  java.lang.String getUiName();
  void setUiName(java.lang.String value);
}
