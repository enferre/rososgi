package vrep_common;

public interface simRosRemoveObjectResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveObjectResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
