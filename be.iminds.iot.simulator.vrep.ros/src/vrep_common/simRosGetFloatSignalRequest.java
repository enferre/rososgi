package vrep_common;

public interface simRosGetFloatSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetFloatSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetFloatSignal(const simChar* signalName,simFloat* signalValue)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
