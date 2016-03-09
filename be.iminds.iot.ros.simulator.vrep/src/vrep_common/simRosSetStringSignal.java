package vrep_common;

public interface simRosSetStringSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetStringSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetStringSignal(const simChar* signalName,const simChar* signalValue,simInt stringLength)\n#\n\nstring signalName\nstring signalValue\n---\nint32 result\n";
}
