package vrep_common;

public interface simRosSetSphericalJointMatrixRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetSphericalJointMatrixRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetSphericalJointMatrix(simInt objectHandle,simFloat* matrix)\n#\n\nint32 handle\ngeometry_msgs/Quaternion quaternion\n";
  int getHandle();
  void setHandle(int value);
  geometry_msgs.Quaternion getQuaternion();
  void setQuaternion(geometry_msgs.Quaternion value);
}
