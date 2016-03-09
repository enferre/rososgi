package vrep_common;

public interface simRosGetObjectParentRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectParentRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectParent(simInt objectHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
