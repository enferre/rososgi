package franka_control;

public interface SetForceTorqueCollisionBehaviorResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "franka_control/SetForceTorqueCollisionBehaviorResponse";
  static final java.lang.String _DEFINITION = "bool success\nstring error";
  boolean getSuccess();
  void setSuccess(boolean value);
  java.lang.String getError();
  void setError(java.lang.String value);
}
