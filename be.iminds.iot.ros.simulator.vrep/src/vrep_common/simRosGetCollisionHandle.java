package vrep_common;

public interface simRosGetCollisionHandle extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetCollisionHandle";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetCollisionHandle(const simChar* collisionObjectName)\n#\n\nstring collisionName\n---\nint32 handle\n";
}
