package vrep_common;

public interface simRosReadForceSensorRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadForceSensorRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadForceSensor(simInt objectHandle,simFloat* forceVector,simFloat* torqueVector)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
