package vrep_common;

public interface simRosRemoveModel extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveModel";
  static final java.lang.String _DEFINITION = "#\n# simInt simRemoveModel(simInt objectHandle)\n#\n\nint32 handle\n---\nint32 result\n";
}
