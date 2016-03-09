package vrep_common;

public interface VisionSensorDepthBuff extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/VisionSensorDepthBuff";
  static final java.lang.String _DEFINITION = "std_msgs/Int32 x\nstd_msgs/Int32 y\nstd_msgs/Float32MultiArray data";
  std_msgs.Int32 getX();
  void setX(std_msgs.Int32 value);
  std_msgs.Int32 getY();
  void setY(std_msgs.Int32 value);
  std_msgs.Float32MultiArray getData();
  void setData(std_msgs.Float32MultiArray value);
}
