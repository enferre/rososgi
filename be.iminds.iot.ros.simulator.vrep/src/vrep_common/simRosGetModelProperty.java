package vrep_common;

public interface simRosGetModelProperty extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetModelProperty";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetModelProperty(simInt objectHandle)\n#\n\nint32 handle\n---\nint32 propertyValue\n";
}
