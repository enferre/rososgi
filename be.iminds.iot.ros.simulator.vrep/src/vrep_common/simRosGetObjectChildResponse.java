package vrep_common;

public interface simRosGetObjectChildResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectChildResponse";
  static final java.lang.String _DEFINITION = "int32 childHandle";
  int getChildHandle();
  void setChildHandle(int value);
}
