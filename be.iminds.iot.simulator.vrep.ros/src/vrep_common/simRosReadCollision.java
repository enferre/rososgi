package vrep_common;

public interface simRosReadCollision extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadCollision";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadCollision(simInt collisionObjectHandle)\n#\n\nint32 handle\n---\nint32 collisionState\n";
}
