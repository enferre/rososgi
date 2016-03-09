package vrep_common;

public interface simRosReadCollisionResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadCollisionResponse";
  static final java.lang.String _DEFINITION = "int32 collisionState";
  int getCollisionState();
  void setCollisionState(int value);
}
