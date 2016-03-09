package vrep_common;

public interface simRosSetObjectSelectionRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectSelectionRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxSetObjectSelection(const simxInt* objectHandles,simxInt objectCount,simxInt operationMode)\n#\n\nint32[] handles\n";
  int[] getHandles();
  void setHandles(int[] value);
}
