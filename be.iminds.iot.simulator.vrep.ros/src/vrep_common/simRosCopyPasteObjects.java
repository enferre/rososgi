package vrep_common;

public interface simRosCopyPasteObjects extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCopyPasteObjects";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxCopyPasteObjects(const simxInt* objectHandles,simxInt objectCount,simxInt** newObjectHandles,simxInt* newObjectCount)\n#\n\nint32[] objectHandles\n---\nint32 result\nint32[] newObjectHandles\n";
}
