package vrep_common;

public interface simRosLoadModelResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadModelResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32 baseHandle";
  int getResult();
  void setResult(int value);
  int getBaseHandle();
  void setBaseHandle(int value);
}
