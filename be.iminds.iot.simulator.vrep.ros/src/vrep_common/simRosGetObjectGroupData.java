package vrep_common;

public interface simRosGetObjectGroupData extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectGroupData";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetObjectGroupData(simxInt clientID,simxInt objectType,simxInt dataType,simxInt* handlesCount,simxInt** handles,simxInt* intDataCount,simxInt** intData,simxInt* floatDataCount,simxFloat** floatData,simxInt* stringDataCount,simxChar** stringData,simxInt operationMode)\n#\n\nint32 objectType\nint32 dataType\n---\nint32[] handles\nint32[] intData\nfloat32[] floatData\nstring[] strings\n";
}
