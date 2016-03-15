package vrep_common;

public interface simRosRemoveUIRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveUIRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simRemoveUI(simInt uiHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
