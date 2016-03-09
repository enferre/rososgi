package vrep_common;

public interface simRosGetObjectsResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectsResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32[] handles";
  int getResult();
  void setResult(int value);
  int[] getHandles();
  void setHandles(int[] value);
}
