package vrep_common;

public interface simRosEraseFileRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEraseFileRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxEraseFile(const simxChar* fileName_serverSide,simxInt operationMode)\n#\n\nstring fileName\n";
  java.lang.String getFileName();
  void setFileName(java.lang.String value);
}
