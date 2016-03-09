package vrep_common;

public interface simRosGetUIEventButtonRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIEventButtonRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIEventButton(simInt uiHandle,simInt* auxiliaryValues)\n#\n\nint32 uiHandle\n";
  int getUiHandle();
  void setUiHandle(int value);
}
