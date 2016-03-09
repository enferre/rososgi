package vrep_common;

public interface simRosGetIntegerSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetIntegerSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetIntegerSignal(const simChar* signalName,simInt* signalValue)\n#\n\nstring signalName\n---\nint32 result\nint32 signalValue\n";
}
