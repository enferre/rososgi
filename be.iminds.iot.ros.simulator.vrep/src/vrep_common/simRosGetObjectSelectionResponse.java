package vrep_common;

public interface simRosGetObjectSelectionResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectSelectionResponse";
  static final java.lang.String _DEFINITION = "int32[] handles";
  int[] getHandles();
  void setHandles(int[] value);
}
