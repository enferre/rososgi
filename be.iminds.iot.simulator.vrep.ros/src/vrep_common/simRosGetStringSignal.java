package vrep_common;

public interface simRosGetStringSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetStringSignal";
  static final java.lang.String _DEFINITION = "#\n# simChar* simGetStringSignal(const simChar* signalName,simInt* stringLength)\n#\n\nstring signalName\n---\nint32 result\nstring signalValue\n";
}
