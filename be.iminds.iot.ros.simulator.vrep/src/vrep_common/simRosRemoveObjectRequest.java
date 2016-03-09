package vrep_common;

public interface simRosRemoveObjectRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosRemoveObjectRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simRemoveObject(simInt objectHandle)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
