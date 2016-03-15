package vrep_common;

public interface simRosGetJointMatrixResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointMatrixResponse";
  static final java.lang.String _DEFINITION = "int32 result\ngeometry_msgs/TransformStamped transform";
  int getResult();
  void setResult(int value);
  geometry_msgs.TransformStamped getTransform();
  void setTransform(geometry_msgs.TransformStamped value);
}
