package vrep_common;

public interface simRosGetObjectGroupDataResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectGroupDataResponse";
  static final java.lang.String _DEFINITION = "int32[] handles\nint32[] intData\nfloat32[] floatData\nstring[] strings";
  int[] getHandles();
  void setHandles(int[] value);
  int[] getIntData();
  void setIntData(int[] value);
  float[] getFloatData();
  void setFloatData(float[] value);
  java.util.List<java.lang.String> getStrings();
  void setStrings(java.util.List<java.lang.String> value);
}
