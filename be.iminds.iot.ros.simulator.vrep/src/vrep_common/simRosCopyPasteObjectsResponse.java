package vrep_common;

public interface simRosCopyPasteObjectsResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCopyPasteObjectsResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32[] newObjectHandles";
  int getResult();
  void setResult(int value);
  int[] getNewObjectHandles();
  void setNewObjectHandles(int[] value);
}
