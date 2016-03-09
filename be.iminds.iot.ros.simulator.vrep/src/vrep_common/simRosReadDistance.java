package vrep_common;

public interface simRosReadDistance extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadDistance";
  static final java.lang.String _DEFINITION = "#\n# simInt simReadDistance(simInt distanceObjectHandle,simFloat* smallestDistance)\n#\n\nint32 handle\n---\nint32 result\nfloat32 distance";
}
