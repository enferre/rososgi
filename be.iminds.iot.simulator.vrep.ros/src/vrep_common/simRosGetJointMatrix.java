package vrep_common;

public interface simRosGetJointMatrix extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointMatrix";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetJointMatrix(simInt objectHandle,simFloat* matrix)\n#\n\nint32 handle\n---\nint32 result\ngeometry_msgs/TransformStamped transform\n";
}
