package vrep_common;

public interface simRosDisablePublisherRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisablePublisherRequest";
  static final java.lang.String _DEFINITION = "#\n#\n#\n\nstring topicName\n";
  java.lang.String getTopicName();
  void setTopicName(java.lang.String value);
}
