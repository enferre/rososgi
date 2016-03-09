package vrep_common;

public interface simRosSetSphericalJointMatrix extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetSphericalJointMatrix";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetSphericalJointMatrix(simInt objectHandle,simFloat* matrix)\n#\n\nint32 handle\ngeometry_msgs/Quaternion quaternion\n---\nint32 result\n";
}
