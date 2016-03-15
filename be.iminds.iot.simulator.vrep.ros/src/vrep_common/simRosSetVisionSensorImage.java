package vrep_common;

public interface simRosSetVisionSensorImage extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetVisionSensorImage";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetVisionSensorImage(simInt sensorHandle,simFloat* image)\n#\n\nint32 handle\nsensor_msgs/Image image\n---\nint32 result\n";
}
