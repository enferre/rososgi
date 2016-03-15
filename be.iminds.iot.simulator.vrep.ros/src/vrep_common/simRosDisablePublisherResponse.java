package vrep_common;

public interface simRosDisablePublisherResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisablePublisherResponse";
  static final java.lang.String _DEFINITION = "int32 referenceCounter";
  int getReferenceCounter();
  void setReferenceCounter(int value);
}
