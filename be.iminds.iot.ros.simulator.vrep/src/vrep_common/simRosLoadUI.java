package vrep_common;

public interface simRosLoadUI extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadUI";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadUI(const simxChar* uiPathAndName,simxChar options,simxInt* count,simxInt** uiHandles,simxInt operationMode)\n#\n\nstring fileName\n---\nint32 result\nint32[] uiHandles\n";
}
