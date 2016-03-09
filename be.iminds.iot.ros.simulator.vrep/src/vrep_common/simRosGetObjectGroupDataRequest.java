package vrep_common;

public interface simRosGetObjectGroupDataRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectGroupDataRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetObjectGroupData(simxInt clientID,simxInt objectType,simxInt dataType,simxInt* handlesCount,simxInt** handles,simxInt* intDataCount,simxInt** intData,simxInt* floatDataCount,simxFloat** floatData,simxInt* stringDataCount,simxChar** stringData,simxInt operationMode)\n#\n\nint32 objectType\nint32 dataType\n";
  int getObjectType();
  void setObjectType(int value);
  int getDataType();
  void setDataType(int value);
}
