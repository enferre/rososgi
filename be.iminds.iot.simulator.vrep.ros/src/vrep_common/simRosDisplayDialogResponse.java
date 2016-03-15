package vrep_common;

public interface simRosDisplayDialogResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisplayDialogResponse";
  static final java.lang.String _DEFINITION = "int32 dialogHandle\nint32 uiHandle";
  int getDialogHandle();
  void setDialogHandle(int value);
  int getUiHandle();
  void setUiHandle(int value);
}
