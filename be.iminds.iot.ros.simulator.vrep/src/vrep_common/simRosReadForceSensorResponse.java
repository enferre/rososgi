package vrep_common;

public interface simRosReadForceSensorResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadForceSensorResponse";
  static final java.lang.String _DEFINITION = "int32 result\ngeometry_msgs/Vector3 force\ngeometry_msgs/Vector3 torque";
  int getResult();
  void setResult(int value);
  geometry_msgs.Vector3 getForce();
  void setForce(geometry_msgs.Vector3 value);
  geometry_msgs.Vector3 getTorque();
  void setTorque(geometry_msgs.Vector3 value);
}
