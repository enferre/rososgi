package vrep_common;

public interface simRosClearStringSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosClearStringSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simClearStringSignal(const simChar* signalName)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
