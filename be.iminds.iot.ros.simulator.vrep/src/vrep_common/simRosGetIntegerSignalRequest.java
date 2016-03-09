package vrep_common;

public interface simRosGetIntegerSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetIntegerSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetIntegerSignal(const simChar* signalName,simInt* signalValue)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
