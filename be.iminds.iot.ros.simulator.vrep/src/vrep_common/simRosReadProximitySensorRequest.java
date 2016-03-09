package vrep_common;

public interface simRosReadProximitySensorRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadProximitySensorRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadProximitySensor(simInt sensorHandle,simFloat* detectedPoint,simInt* detectedObjectHandle,simFloat* detectedSurfaceNormalVector)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
