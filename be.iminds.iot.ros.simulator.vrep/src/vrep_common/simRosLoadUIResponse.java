package vrep_common;

public interface simRosLoadUIResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadUIResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32[] uiHandles";
  int getResult();
  void setResult(int value);
  int[] getUiHandles();
  void setUiHandles(int[] value);
}
