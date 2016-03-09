package vrep_common;

public interface simRosGetDialogInput extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetDialogInput";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetDialogInput(simInt genericDialogHandle)\n#\n\nint32 dialogHandle\n---\nint32 result\nstring input\n";
}
