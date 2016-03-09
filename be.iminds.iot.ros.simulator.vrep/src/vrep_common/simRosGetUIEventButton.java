package vrep_common;

public interface simRosGetUIEventButton extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIEventButton";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIEventButton(simInt uiHandle,simInt* auxiliaryValues)\n#\n\nint32 uiHandle\n---\nint32 buttonID\nint32[] auxiliaryValues\n";
}
