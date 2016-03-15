package vrep_common;

public interface simRosSetFloatSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetFloatSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetFloatSignal(const simChar* signalName,simFloat signalValue)\n#\n\nstring signalName\nfloat32 signalValue\n---\nint32 result\n";
}
