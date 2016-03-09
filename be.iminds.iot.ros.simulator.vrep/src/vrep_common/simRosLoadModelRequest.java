package vrep_common;

public interface simRosLoadModelRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadModelRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadModel(const simxChar* modelPathAndName,simxChar options,simxInt* baseHandle,simxInt operationMode)\n#\n\nstring fileName\n";
  java.lang.String getFileName();
  void setFileName(java.lang.String value);
}
