package vrep_common;

public interface simRosGetStringSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetStringSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetStringSignal(const simChar* signalName,simInt* stringLength)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
