package vrep_common;

public interface simRosAppendStringSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAppendStringSignal";
  static final java.lang.String _DEFINITION = "#\n# simInt simAppendStringSignal(const simChar* signalName,const simChar* signalValue,simInt stringLength)\n#\n\nstring signalName\nstring signalValue\n---\nint32 result\n";
}
