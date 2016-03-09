package vrep_common;

public interface simRosClearFloatSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosClearFloatSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simClearFloatSignal(const simChar* signalName)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
