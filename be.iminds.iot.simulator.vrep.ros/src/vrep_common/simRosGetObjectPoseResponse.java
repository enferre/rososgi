package vrep_common;

public interface simRosGetObjectPoseResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectPoseResponse";
  static final java.lang.String _DEFINITION = "int32 result\ngeometry_msgs/PoseStamped pose";
  int getResult();
  void setResult(int value);
  geometry_msgs.PoseStamped getPose();
  void setPose(geometry_msgs.PoseStamped value);
}
