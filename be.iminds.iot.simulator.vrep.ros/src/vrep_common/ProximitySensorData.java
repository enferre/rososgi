package vrep_common;

public interface ProximitySensorData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/ProximitySensorData";
  static final java.lang.String _DEFINITION = "geometry_msgs/Point32 detectedPoint\nstd_msgs/Int32 detectedObject\ngeometry_msgs/Point32 normalVector\n";
  geometry_msgs.Point32 getDetectedPoint();
  void setDetectedPoint(geometry_msgs.Point32 value);
  std_msgs.Int32 getDetectedObject();
  void setDetectedObject(std_msgs.Int32 value);
  geometry_msgs.Point32 getNormalVector();
  void setNormalVector(geometry_msgs.Point32 value);
}
