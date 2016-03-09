package vrep_common;

public interface simRosSetVisionSensorImageRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetVisionSensorImageRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetVisionSensorImage(simInt sensorHandle,simFloat* image)\n#\n\nint32 handle\nsensor_msgs/Image image\n";
  int getHandle();
  void setHandle(int value);
  sensor_msgs.Image getImage();
  void setImage(sensor_msgs.Image value);
}
