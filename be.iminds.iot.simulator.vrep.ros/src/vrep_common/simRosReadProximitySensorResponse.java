package vrep_common;

public interface simRosReadProximitySensorResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadProximitySensorResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32[] detectedPoint\nint32 detectedObject\nfloat32[] normalVector";
  int getResult();
  void setResult(int value);
  float[] getDetectedPoint();
  void setDetectedPoint(float[] value);
  int getDetectedObject();
  void setDetectedObject(int value);
  float[] getNormalVector();
  void setNormalVector(float[] value);
}
