package vrep_common;

public interface simRosCloseSceneResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCloseSceneResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
