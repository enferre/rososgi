package vrep_common;

public interface simRosSetFloatSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetFloatSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetFloatSignal(const simChar* signalName,simFloat signalValue)\n#\n\nstring signalName\nfloat32 signalValue\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
  float getSignalValue();
  void setSignalValue(float value);
}
