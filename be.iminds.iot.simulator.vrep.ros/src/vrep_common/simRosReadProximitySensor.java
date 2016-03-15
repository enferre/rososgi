package vrep_common;

public interface simRosReadProximitySensor extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadProximitySensor";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadProximitySensor(simInt sensorHandle,simFloat* detectedPoint,simInt* detectedObjectHandle,simFloat* detectedSurfaceNormalVector)\n#\n\nint32 handle\n---\nint32 result\nfloat32[] detectedPoint\nint32 detectedObject\nfloat32[] normalVector\n";
}
