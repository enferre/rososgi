package vrep_common;

public interface simRosCreateDummyResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCreateDummyResponse";
  static final java.lang.String _DEFINITION = "int32 dummyHandle";
  int getDummyHandle();
  void setDummyHandle(int value);
}
