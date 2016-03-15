package vrep_common;

public interface simRosGetIntegerSignalResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetIntegerSignalResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32 signalValue";
  int getResult();
  void setResult(int value);
  int getSignalValue();
  void setSignalValue(int value);
}
