package vrep_common;

public interface simRosGetVisionSensorDepthBufferResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorDepthBufferResponse";
  static final java.lang.String _DEFINITION = "int32 result\nint32[] resolution\nfloat32[] buffer";
  int getResult();
  void setResult(int value);
  int[] getResolution();
  void setResolution(int[] value);
  float[] getBuffer();
  void setBuffer(float[] value);
}
