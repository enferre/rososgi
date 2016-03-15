package vrep_common;

public interface simRosLoadSceneResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosLoadSceneResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
