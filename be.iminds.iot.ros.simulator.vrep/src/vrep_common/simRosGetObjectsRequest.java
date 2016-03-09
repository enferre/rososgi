package vrep_common;

public interface simRosGetObjectsRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectsRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetObjects(simxInt objectType,simxInt* objectCount,simxInt** objectHandles,simxInt operationMode)\n#\n\nint32 objectType\n";
  int getObjectType();
  void setObjectType(int value);
}
