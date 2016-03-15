package vrep_common;

public interface simRosCreateDummy extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosCreateDummy";
  static final java.lang.String _DEFINITION = "#\n# simInt simCreateDummy(simFloat size,const simFloat* colors)\n#\n\nfloat32 size\nint8[] colors\n---\nint32 dummyHandle\n\n";
}
