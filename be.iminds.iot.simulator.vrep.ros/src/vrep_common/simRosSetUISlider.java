package vrep_common;

public interface simRosSetUISlider extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUISlider";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUISlider(simInt uiHandle,simInt buttonHandle,simInt position)\n#\n\nint32 uiHandle\nint32 buttonID\nint32 position\n---\nint32 result\n";
}
