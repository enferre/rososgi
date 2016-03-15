package vrep_common;

public interface simRosReadVisionSensorRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadVisionSensorRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadVisionSensor(simInt visionSensorHandle,simFloat** auxValues,simInt** auxValuesCount)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
