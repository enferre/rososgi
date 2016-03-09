package vrep_common;

public interface simRosSetIntegerSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetIntegerSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetIntegerSignal(const simChar* signalName,simInt signalValue)\n#\n\nstring signalName\nint32 signalValue\n---\nint32 result\n";
}
