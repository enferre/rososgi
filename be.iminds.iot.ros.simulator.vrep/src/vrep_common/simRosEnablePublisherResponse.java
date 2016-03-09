package vrep_common;

public interface simRosEnablePublisherResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEnablePublisherResponse";
  static final java.lang.String _DEFINITION = "string effectiveTopicName";
  java.lang.String getEffectiveTopicName();
  void setEffectiveTopicName(java.lang.String value);
}
