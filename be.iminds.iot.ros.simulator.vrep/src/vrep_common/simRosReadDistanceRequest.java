package vrep_common;

public interface simRosReadDistanceRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadDistanceRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadDistance(simInt distanceObjectHandle,simFloat* smallestDistance)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
