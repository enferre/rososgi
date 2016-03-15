package vrep_common;

public interface simRosGetUISliderRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUISliderRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUISlider(simInt uiHandle,simInt buttonHandle)\n#\n\nint32 uiHandle\nint32 buttonID\n";
  int getUiHandle();
  void setUiHandle(int value);
  int getButtonID();
  void setButtonID(int value);
}
