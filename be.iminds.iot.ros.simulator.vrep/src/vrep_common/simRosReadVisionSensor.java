package vrep_common;

public interface simRosReadVisionSensor extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadVisionSensor";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadVisionSensor(simInt visionSensorHandle,simFloat** auxValues,simInt** auxValuesCount)\n#\n\nint32 handle\n---\nint32 result\nfloat32[] packetData\nint32[] packetSizes\n";
}
