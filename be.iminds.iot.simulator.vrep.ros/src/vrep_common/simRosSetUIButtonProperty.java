package vrep_common;

public interface simRosSetUIButtonProperty extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetUIButtonProperty";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetUIButtonProperty(simInt uiHandle,simInt buttonHandle,simInt buttonProperty)\n#\n\nint32 uiHandle\nint32 buttonID\nint32 propertyValue\n---\nint32 result\n";
}
