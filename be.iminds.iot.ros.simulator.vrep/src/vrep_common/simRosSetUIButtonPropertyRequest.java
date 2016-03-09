package vrep_common;

public interface simRosSetUIButtonPropertyRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUIButtonPropertyRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUIButtonProperty(simInt uiHandle,simInt buttonHandle,simInt buttonProperty)\n#\n\nint32 uiHandle\nint32 buttonID\nint32 propertyValue\n";
  int getUiHandle();
  void setUiHandle(int value);
  int getButtonID();
  void setButtonID(int value);
  int getPropertyValue();
  void setPropertyValue(int value);
}
