package vrep_common;

public interface simRosGetUISlider extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUISlider";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUISlider(simInt uiHandle,simInt buttonHandle)\n#\n\nint32 uiHandle\nint32 buttonID\n---\nint32 position\n";
}
