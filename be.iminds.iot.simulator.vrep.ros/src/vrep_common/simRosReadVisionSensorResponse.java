package vrep_common;

public interface simRosReadVisionSensorResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosReadVisionSensorResponse";
  static final java.lang.String _DEFINITION = "int32 result\nfloat32[] packetData\nint32[] packetSizes";
  int getResult();
  void setResult(int value);
  float[] getPacketData();
  void setPacketData(float[] value);
  int[] getPacketSizes();
  void setPacketSizes(int[] value);
}
