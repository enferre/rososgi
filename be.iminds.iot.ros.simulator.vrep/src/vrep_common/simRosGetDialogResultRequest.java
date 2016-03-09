package vrep_common;

public interface simRosGetDialogResultRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetDialogResultRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetDialogResult(simInt genericDialogHandle)\n#\n\nint32 dialogHandle\n";
  int getDialogHandle();
  void setDialogHandle(int value);
}
