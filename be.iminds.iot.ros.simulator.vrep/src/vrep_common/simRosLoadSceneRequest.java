package vrep_common;

public interface simRosLoadSceneRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadSceneRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadScene(const simxChar* scenePathAndName,simxChar options,simxInt operationMode)\n#\n\nstring fileName\n";
  java.lang.String getFileName();
  void setFileName(java.lang.String value);
}
