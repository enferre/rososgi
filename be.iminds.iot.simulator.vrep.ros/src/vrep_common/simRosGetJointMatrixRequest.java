package vrep_common;

public interface simRosGetJointMatrixRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetJointMatrixRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetJointMatrix(simInt objectHandle,simFloat* matrix)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
