package vrep_common;

public interface simRosGetAndClearStringSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetAndClearStringSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simxChar* simxGetAndClearStringSignal(const simxChar* signalName,simxInt* stringLength)\n#\n\nstring signalName\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
}
