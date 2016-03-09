package vrep_common;

public interface simRosDisableSubscriberResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisableSubscriberResponse";
  static final java.lang.String _DEFINITION = "uint8 result";
  byte getResult();
  void setResult(byte value);
}
