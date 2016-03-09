package vrep_common;

public interface simRosBreakForceSensorRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosBreakForceSensorRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simBreakForceSensor(simInt objectHandle)\n#\n\nint32 objectHandle\n";
  int getObjectHandle();
  void setObjectHandle(int value);
}
