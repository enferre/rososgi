package vrep_common;

public interface simRosClearIntegerSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosClearIntegerSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simClearIntegerSignal(const simChar* signalName)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
