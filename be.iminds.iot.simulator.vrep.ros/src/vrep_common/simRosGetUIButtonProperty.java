package vrep_common;

public interface simRosGetUIButtonProperty extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIButtonProperty";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetUIButtonProperty(simInt uiHandle,simInt buttonHandle)\n#\n\nint32 uiHandle\nint32 buttonID\n---\nint32 propertyValue\n";
}
