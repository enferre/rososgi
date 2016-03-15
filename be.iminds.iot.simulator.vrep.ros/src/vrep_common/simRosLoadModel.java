package vrep_common;

public interface simRosLoadModel extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadModel";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxLoadModel(const simxChar* modelPathAndName,simxChar options,simxInt* baseHandle,simxInt operationMode)\n#\n\nstring fileName\n---\nint32 result\nint32 baseHandle\n";
}
