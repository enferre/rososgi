package vrep_common;

public interface simRosGetVisionSensorImage extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorImage";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetVisionSensorImage(simxInt sensorHandle,simxInt* resolution,simxChar** image,simxChar options,simxInt operationMode)\n#\n\nint32 handle\nuint8 options\n---\nint32 result\nsensor_msgs/Image image\n";
}
