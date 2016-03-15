package vrep_common;

public interface simRosStartSimulationResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosStartSimulationResponse";
  static final java.lang.String _DEFINITION = "int32 result";
  int getResult();
  void setResult(int value);
}
