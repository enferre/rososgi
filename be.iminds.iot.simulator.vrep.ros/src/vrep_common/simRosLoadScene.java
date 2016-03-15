package vrep_common;

public interface simRosLoadScene extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadScene";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadScene(const simxChar* scenePathAndName,simxChar options,simxInt operationMode)\n#\n\nstring fileName\n---\nint32 result\n";
}
