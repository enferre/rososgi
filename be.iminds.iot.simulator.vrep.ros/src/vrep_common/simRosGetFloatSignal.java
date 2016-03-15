package vrep_common;

public interface simRosGetFloatSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetFloatSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetFloatSignal(const simChar* signalName,simFloat* signalValue)\n#\n\nstring signalName\n---\nint32 result\nfloat32 signalValue\n";
}
