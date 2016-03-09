package vrep_common;

public interface simRosGetObjectHandle extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectHandle";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectHandle(const simChar* objectName)\n#\n\nstring objectName\n---\nint32 handle\n";
}
