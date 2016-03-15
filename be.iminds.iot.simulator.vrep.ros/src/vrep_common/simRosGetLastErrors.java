package vrep_common;

public interface simRosGetLastErrors extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetLastErrors";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetLastErrors(simxInt* errorCnt,simxChar** errorStrings,simxInt operationMode)\n#\n\n\n---\nint32 errorCnt\nstring errorStrings\n";
}
