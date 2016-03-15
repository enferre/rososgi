package vrep_common;

public interface ForceSensorData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/ForceSensorData";
  static final java.lang.String _DEFINITION = "std_msgs/Int32 sensorState\ngeometry_msgs/Vector3 force\ngeometry_msgs/Vector3 torque\n";
  std_msgs.Int32 getSensorState();
  void setSensorState(std_msgs.Int32 value);
  geometry_msgs.Vector3 getForce();
  void setForce(geometry_msgs.Vector3 value);
  geometry_msgs.Vector3 getTorque();
  void setTorque(geometry_msgs.Vector3 value);
}
