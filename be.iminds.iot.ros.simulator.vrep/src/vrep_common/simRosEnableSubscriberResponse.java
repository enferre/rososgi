package vrep_common;

public interface simRosEnableSubscriberResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosEnableSubscriberResponse";
  static final java.lang.String _DEFINITION = "int32 subscriberID";
  int getSubscriberID();
  void setSubscriberID(int value);
}
