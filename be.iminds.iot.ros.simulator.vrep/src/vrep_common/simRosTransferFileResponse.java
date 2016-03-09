package vrep_common;

public interface simRosTransferFileResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosTransferFileResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
