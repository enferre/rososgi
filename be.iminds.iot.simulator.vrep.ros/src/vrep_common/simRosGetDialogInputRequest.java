package vrep_common;

public interface simRosGetDialogInputRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetDialogInputRequest";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetDialogInput(simInt genericDialogHandle)\n#\n\nint32 dialogHandle\n";
  int getDialogHandle();
  void setDialogHandle(int value);
}
