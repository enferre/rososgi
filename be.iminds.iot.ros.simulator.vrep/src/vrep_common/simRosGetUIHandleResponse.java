package vrep_common;

public interface simRosGetUIHandleResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIHandleResponse";
  static final java.lang.String _DEFINITION = "int32 handle";
  int getHandle();
  void setHandle(int value);
}
