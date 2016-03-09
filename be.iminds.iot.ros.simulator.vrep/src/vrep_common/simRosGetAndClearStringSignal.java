package vrep_common;

public interface simRosGetAndClearStringSignal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetAndClearStringSignal";
  static final java.lang.String _DEFINITION = "#\n# simxChar* simxGetAndClearStringSignal(const simxChar* signalName,simxInt* stringLength)\n#\n\nstring signalName\n---\nint32 result\nstring signalValue\n";
}
