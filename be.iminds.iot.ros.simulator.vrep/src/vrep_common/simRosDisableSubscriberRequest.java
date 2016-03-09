package vrep_common;

public interface simRosDisableSubscriberRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisableSubscriberRequest";
  static final java.lang.String _DEFINITION = "#\n#\n#\n\nint32 subscriberID\n";
  int getSubscriberID();
  void setSubscriberID(int value);
}
