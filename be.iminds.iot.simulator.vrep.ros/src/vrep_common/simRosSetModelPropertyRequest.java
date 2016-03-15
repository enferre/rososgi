package vrep_common;

public interface simRosSetModelPropertyRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetModelPropertyRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetModelProperty(simInt objectHandle,simInt prop)\n#\n\nint32 handle\nint32 propertyValue\n";
  int getHandle();
  void setHandle(int value);
  int getPropertyValue();
  void setPropertyValue(int value);
}
