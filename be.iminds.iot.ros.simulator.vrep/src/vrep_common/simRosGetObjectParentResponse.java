package vrep_common;

public interface simRosGetObjectParentResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectParentResponse";
  static final java.lang.String _DEFINITION = "int32 parentHandle";
  int getParentHandle();
  void setParentHandle(int value);
}
