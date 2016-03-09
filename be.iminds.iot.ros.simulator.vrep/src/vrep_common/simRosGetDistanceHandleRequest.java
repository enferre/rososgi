package vrep_common;

public interface simRosGetDistanceHandleRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetDistanceHandleRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetDistanceHandle(const simChar* distanceObjectName)\n#\n\nstring distanceName\n";
  java.lang.String getDistanceName();
  void setDistanceName(java.lang.String value);
}
