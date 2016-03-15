package vrep_common;

public interface simRosGetObjects extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjects";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetObjects(simxInt objectType,simxInt* objectCount,simxInt** objectHandles,simxInt operationMode)\n#\n\nint32 objectType\n---\nint32 result\nint32[] handles\n";
}
