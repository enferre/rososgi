package vrep_common;

public interface simRosEnableSubscriber extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEnableSubscriber";
  static final java.lang.String _DEFINITION = "#\n# \n#\n\nstring topicName\nint32 queueSize\nint32 streamCmd\nint32 auxInt1\nint32 auxInt2\nstring auxString\n---\nint32 subscriberID\n";
}
