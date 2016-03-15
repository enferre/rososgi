package vrep_common;

public interface simRosTransferFileRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosTransferFileRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxTransferFile(const simxChar* filePathAndName,const simxChar* fileName_serverSide,simxInt timeOut,simxInt operationMode)\n#\n\nuint8[] data\nstring fileName\n";
  org.jboss.netty.buffer.ChannelBuffer getData();
  void setData(org.jboss.netty.buffer.ChannelBuffer value);
  java.lang.String getFileName();
  void setFileName(java.lang.String value);
}
