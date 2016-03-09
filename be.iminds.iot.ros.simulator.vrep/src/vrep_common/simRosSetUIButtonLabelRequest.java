package vrep_common;

public interface simRosSetUIButtonLabelRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUIButtonLabelRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUIButtonLabel(simInt uiHandle,simInt buttonHandle,const simChar* upStateLabel,const simChar* downStateLabel)\n#\n\nint32 uiHandle\nint32 buttonID\nstring upStateLabel\nstring downStateLabel\n";
  int getUiHandle();
  void setUiHandle(int value);
  int getButtonID();
  void setButtonID(int value);
  java.lang.String getUpStateLabel();
  void setUpStateLabel(java.lang.String value);
  java.lang.String getDownStateLabel();
  void setDownStateLabel(java.lang.String value);
}
