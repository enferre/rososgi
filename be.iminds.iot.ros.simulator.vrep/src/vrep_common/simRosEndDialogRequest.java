package vrep_common;

public interface simRosEndDialogRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEndDialogRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simEndDialog(simInt genericDialogHandle)\n#\n\nint32 dialogHandle\n";
  int getDialogHandle();
  void setDialogHandle(int value);
}
