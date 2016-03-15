package vrep_common;

public interface simRosAddStatusbarMessageRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAddStatusbarMessageRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simAddStatusbarMessage(const simChar* message)\n#\n\nstring message\n";
  java.lang.String getMessage();
  void setMessage(java.lang.String value);
}
