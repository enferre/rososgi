package vrep_common;

public interface simRosLoadUIRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadUIRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadUI(const simxChar* uiPathAndName,simxChar options,simxInt* count,simxInt** uiHandles,simxInt operationMode)\n#\n\nstring fileName\n";
  java.lang.String getFileName();
  void setFileName(java.lang.String value);
}
