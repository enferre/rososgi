package vrep_common;

public interface simRosReadCollisionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadCollisionRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadCollision(simInt collisionObjectHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
