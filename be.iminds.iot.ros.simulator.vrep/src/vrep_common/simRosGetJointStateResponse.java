package vrep_common;

public interface simRosGetJointStateResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointStateResponse";
  static final java.lang.String _DEFINITION = "int32 result\nsensor_msgs/JointState state";
  int getResult();
  void setResult(int value);
  sensor_msgs.JointState getState();
  void setState(sensor_msgs.JointState value);
}
