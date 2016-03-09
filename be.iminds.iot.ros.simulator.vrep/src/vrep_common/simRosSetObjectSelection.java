package vrep_common;

public interface simRosSetObjectSelection extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectSelection";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxSetObjectSelection(const simxInt* objectHandles,simxInt objectCount,simxInt operationMode)\n#\n\nint32[] handles\n---\nint32 result\n";
}
