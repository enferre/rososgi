package vrep_common;

public interface simRosEnablePublisher extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEnablePublisher";
  static final java.lang.String _DEFINITION = "#\n# \n#\n\nstring topicName\nint32 queueSize\nint32 streamCmd\nint32 auxInt1\nint32 auxInt2\nstring auxString\n---\nstring effectiveTopicName\n";
}
