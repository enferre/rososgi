package vrep_common;

public interface simRosCopyPasteObjectsRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCopyPasteObjectsRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxCopyPasteObjects(const simxInt* objectHandles,simxInt objectCount,simxInt** newObjectHandles,simxInt* newObjectCount)\n#\n\nint32[] objectHandles\n";
  int[] getObjectHandles();
  void setObjectHandles(int[] value);
}
