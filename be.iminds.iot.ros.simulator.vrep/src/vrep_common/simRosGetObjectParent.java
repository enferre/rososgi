package vrep_common;

public interface simRosGetObjectParent extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectParent";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectParent(simInt objectHandle)\n#\n\nint32 handle\n---\nint32 parentHandle\n";
}
