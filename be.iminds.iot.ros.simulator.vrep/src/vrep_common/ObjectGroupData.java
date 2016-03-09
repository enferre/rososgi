package vrep_common;

public interface ObjectGroupData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/ObjectGroupData";
  static final java.lang.String _DEFINITION = "std_msgs/Int32MultiArray handles\nstd_msgs/Int32MultiArray intData\nstd_msgs/Float32MultiArray floatData\nstd_msgs/String stringData\n";
  std_msgs.Int32MultiArray getHandles();
  void setHandles(std_msgs.Int32MultiArray value);
  std_msgs.Int32MultiArray getIntData();
  void setIntData(std_msgs.Int32MultiArray value);
  std_msgs.Float32MultiArray getFloatData();
  void setFloatData(std_msgs.Float32MultiArray value);
  std_msgs.String getStringData();
  void setStringData(std_msgs.String value);
}
