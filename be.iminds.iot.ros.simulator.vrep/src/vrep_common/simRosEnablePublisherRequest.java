package vrep_common;

public interface simRosEnablePublisherRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEnablePublisherRequest";
  static final java.lang.String _DEFINITION = "#\n# \n#\n\nstring topicName\nint32 queueSize\nint32 streamCmd\nint32 auxInt1\nint32 auxInt2\nstring auxString\n";
  java.lang.String getTopicName();
  void setTopicName(java.lang.String value);
  int getQueueSize();
  void setQueueSize(int value);
  int getStreamCmd();
  void setStreamCmd(int value);
  int getAuxInt1();
  void setAuxInt1(int value);
  int getAuxInt2();
  void setAuxInt2(int value);
  java.lang.String getAuxString();
  void setAuxString(java.lang.String value);
}
