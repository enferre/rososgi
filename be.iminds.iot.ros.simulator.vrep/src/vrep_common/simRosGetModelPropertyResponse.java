package vrep_common;

public interface simRosGetModelPropertyResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetModelPropertyResponse";
  static final java.lang.String _DEFINITION = "int32 propertyValue";
  int getPropertyValue();
  void setPropertyValue(int value);
}
