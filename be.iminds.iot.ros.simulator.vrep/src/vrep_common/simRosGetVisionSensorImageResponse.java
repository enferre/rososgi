package vrep_common;

public interface simRosGetVisionSensorImageResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorImageResponse";
  static final java.lang.String _DEFINITION = "int32 result\nsensor_msgs/Image image";
  int getResult();
  void setResult(int value);
  sensor_msgs.Image getImage();
  void setImage(sensor_msgs.Image value);
}
