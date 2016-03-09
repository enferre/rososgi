package vrep_common;

public interface simRosRemoveObject extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveObject";
  static final java.lang.String _DEFINITION = "#\n# simInt simRemoveObject(simInt objectHandle)\n#\n\nint32 handle\n---\nint32 result\n";
}
