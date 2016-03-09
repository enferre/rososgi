package vrep_common;

public interface simRosTransferFile extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosTransferFile";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxTransferFile(const simxChar* filePathAndName,const simxChar* fileName_serverSide,simxInt timeOut,simxInt operationMode)\n#\n\nuint8[] data\nstring fileName\n---\nint32 result\n";
}
