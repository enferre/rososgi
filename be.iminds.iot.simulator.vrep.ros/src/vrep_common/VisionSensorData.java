package vrep_common;

public interface VisionSensorData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/VisionSensorData";
  static final java.lang.String _DEFINITION = "std_msgs/Int32 triggerState\nstd_msgs/Float32MultiArray packetData\nstd_msgs/Int32MultiArray packetSizes\n";
  std_msgs.Int32 getTriggerState();
  void setTriggerState(std_msgs.Int32 value);
  std_msgs.Float32MultiArray getPacketData();
  void setPacketData(std_msgs.Float32MultiArray value);
  std_msgs.Int32MultiArray getPacketSizes();
  void setPacketSizes(std_msgs.Int32MultiArray value);
}
