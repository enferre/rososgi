package vrep_common;

public interface simRosGetObjectHandleRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetObjectHandleRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetObjectHandle(const simChar* objectName)\n#\n\nstring objectName\n";
  java.lang.String getObjectName();
  void setObjectName(java.lang.String value);
}
