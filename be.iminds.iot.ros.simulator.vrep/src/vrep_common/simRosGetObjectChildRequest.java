package vrep_common;

public interface simRosGetObjectChildRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectChildRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectChild(simInt objectHandle,simInt index)\n#\n\nint32 handle\nint32 index\n";
  int getHandle();
  void setHandle(int value);
  int getIndex();
  void setIndex(int value);
}
