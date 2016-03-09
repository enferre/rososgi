package vrep_common;

public interface simRosSetIntegerSignalRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetIntegerSignalRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetIntegerSignal(const simChar* signalName,simInt signalValue)\n#\n\nstring signalName\nint32 signalValue\n";
  java.lang.String getSignalName();
  void setSignalName(java.lang.String value);
  int getSignalValue();
  void setSignalValue(int value);
}
