package vrep_common;

public interface simRosReadForceSensor extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadForceSensor";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadForceSensor(simInt objectHandle,simFloat* forceVector,simFloat* torqueVector)\n#\n\nint32 handle\n---\nint32 result\ngeometry_msgs/Vector3 force\ngeometry_msgs/Vector3 torque\n";
}
