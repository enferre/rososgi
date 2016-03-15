package vrep_common;

public interface simRosSetJointStateRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointStateRequest";
  static final java.lang.String _DEFINITION = "#\n# \n# ...\n\nint32[] handles\nuint8[] setModes\nfloat32[] values\n";
  int[] getHandles();
  void setHandles(int[] value);
  org.jboss.netty.buffer.ChannelBuffer getSetModes();
  void setSetModes(org.jboss.netty.buffer.ChannelBuffer value);
  float[] getValues();
  void setValues(float[] value);
}
