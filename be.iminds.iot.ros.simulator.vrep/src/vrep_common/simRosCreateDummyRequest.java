package vrep_common;

public interface simRosCreateDummyRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCreateDummyRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simCreateDummy(simFloat size,const simFloat* colors)\n#\n\nfloat32 size\nint8[] colors\n";
  float getSize();
  void setSize(float value);
  org.jboss.netty.buffer.ChannelBuffer getColors();
  void setColors(org.jboss.netty.buffer.ChannelBuffer value);
}
