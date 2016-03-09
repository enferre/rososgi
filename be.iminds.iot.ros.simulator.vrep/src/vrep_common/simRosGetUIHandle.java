package vrep_common;

public interface simRosGetUIHandle extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIHandle";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIHandle(const simChar* uiName)\n#\n\nstring uiName\n---\nint32 handle\n";
}
