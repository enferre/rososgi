package vrep_common;

public interface simRosSetUIButtonLabel extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUIButtonLabel";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUIButtonLabel(simInt uiHandle,simInt buttonHandle,const simChar* upStateLabel,const simChar* downStateLabel)\n#\n\nint32 uiHandle\nint32 buttonID\nstring upStateLabel\nstring downStateLabel\n---\nint32 result\n";
}
