package vrep_common;

public interface simRosGetVisionSensorImageRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorImageRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetVisionSensorImage(simxInt sensorHandle,simxInt* resolution,simxChar** image,simxChar options,simxInt operationMode)\n#\n\nint32 handle\nuint8 options\n";
  int getHandle();
  void setHandle(int value);
  byte getOptions();
  void setOptions(byte value);
}
