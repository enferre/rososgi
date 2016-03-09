package vrep_common;

public interface simRosSetJointState extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetJointState";
  static final java.lang.String _DEFINITION = "#\n# \n# ...\n\nint32[] handles\nuint8[] setModes\nfloat32[] values\n---\nint32 result\n";
}
