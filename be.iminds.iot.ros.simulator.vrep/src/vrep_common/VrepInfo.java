package vrep_common;

public interface VrepInfo extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/VrepInfo";
  static final java.lang.String _DEFINITION = "std_msgs/Header headerInfo\nstd_msgs/Int32 simulatorState\nstd_msgs/Float32 simulationTime\nstd_msgs/Float32 timeStep\n";
  std_msgs.Header getHeaderInfo();
  void setHeaderInfo(std_msgs.Header value);
  std_msgs.Int32 getSimulatorState();
  void setSimulatorState(std_msgs.Int32 value);
  std_msgs.Float32 getSimulationTime();
  void setSimulationTime(std_msgs.Float32 value);
  std_msgs.Float32 getTimeStep();
  void setTimeStep(std_msgs.Float32 value);
}
