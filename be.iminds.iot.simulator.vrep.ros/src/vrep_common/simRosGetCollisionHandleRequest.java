package vrep_common;

public interface simRosGetCollisionHandleRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetCollisionHandleRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetCollisionHandle(const simChar* collisionObjectName)\n#\n\nstring collisionName\n";
  java.lang.String getCollisionName();
  void setCollisionName(java.lang.String value);
}
