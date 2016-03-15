package vrep_common;

public interface JointSetStateData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/JointSetStateData";
  static final java.lang.String _DEFINITION = "std_msgs/Int32MultiArray handles\nstd_msgs/UInt8MultiArray setModes\nstd_msgs/Float32MultiArray values\n";
  std_msgs.Int32MultiArray getHandles();
  void setHandles(std_msgs.Int32MultiArray value);
  std_msgs.UInt8MultiArray getSetModes();
  void setSetModes(std_msgs.UInt8MultiArray value);
  std_msgs.Float32MultiArray getValues();
  void setValues(std_msgs.Float32MultiArray value);
}
