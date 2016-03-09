package vrep_common;

public interface simRosReadDistanceResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadDistanceResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32 distance";
  int getResult();
  void setResult(int value);
  float getDistance();
  void setDistance(float value);
}
