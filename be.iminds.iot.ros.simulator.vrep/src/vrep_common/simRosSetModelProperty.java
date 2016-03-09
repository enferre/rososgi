package vrep_common;

public interface simRosSetModelProperty extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetModelProperty";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetModelProperty(simInt objectHandle,simInt prop)\n#\n\nint32 handle\nint32 propertyValue\n---\nint32 result\n";
}
