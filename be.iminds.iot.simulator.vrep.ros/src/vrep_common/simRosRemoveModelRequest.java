package vrep_common;

public interface simRosRemoveModelRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveModelRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simRemoveModel(simInt objectHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
