package vrep_common;

public interface simRosGetUIButtonPropertyRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIButtonPropertyRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIButtonProperty(simInt uiHandle,simInt buttonHandle)\n#\n\nint32 uiHandle\nint32 buttonID\n";
  int getUiHandle();
  void setUiHandle(int value);
  int getButtonID();
  void setButtonID(int value);
}
