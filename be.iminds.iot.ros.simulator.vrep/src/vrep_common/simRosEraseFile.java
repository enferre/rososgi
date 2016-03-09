package vrep_common;

public interface simRosEraseFile extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEraseFile";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxEraseFile(const simxChar* fileName_serverSide,simxInt operationMode)\n#\n\nstring fileName\n---\nint32 result\n";
}
