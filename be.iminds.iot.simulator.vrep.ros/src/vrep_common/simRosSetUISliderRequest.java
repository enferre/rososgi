package vrep_common;

public interface simRosSetUISliderRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUISliderRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUISlider(simInt uiHandle,simInt buttonHandle,simInt position)\n#\n\nint32 uiHandle\nint32 buttonID\nint32 position\n";
  int getUiHandle();
  void setUiHandle(int value);
  int getButtonID();
  void setButtonID(int value);
  int getPosition();
  void setPosition(int value);
}
