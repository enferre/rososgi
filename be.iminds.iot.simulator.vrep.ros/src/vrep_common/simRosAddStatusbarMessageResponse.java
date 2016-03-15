package vrep_common;

public interface simRosAddStatusbarMessageResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAddStatusbarMessageResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
