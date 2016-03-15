package vrep_common;

public interface simRosGetModelPropertyRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetModelPropertyRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetModelProperty(simInt objectHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
