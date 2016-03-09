package vrep_common;

public interface simRosSetStringSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetStringSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetStringSignal(const simChar* signalName,const simChar* signalValue,simInt stringLength)\n#\n\nstring signalName\nstring signalValue\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
  java.lang.String getSignalValue();
  void setSignalValue(java.lang.String value);
}
