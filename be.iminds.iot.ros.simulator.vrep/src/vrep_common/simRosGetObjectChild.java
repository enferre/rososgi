package vrep_common;

public interface simRosGetObjectChild extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectChild";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectChild(simInt objectHandle,simInt index)\n#\n\nint32 handle\nint32 index\n---\nint32 childHandle\n";
}
