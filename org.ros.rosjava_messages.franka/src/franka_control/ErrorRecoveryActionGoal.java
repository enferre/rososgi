package franka_control;

public interface ErrorRecoveryActionGoal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "franka_control/ErrorRecoveryActionGoal";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n\nHeader header\nactionlib_msgs/GoalID goal_id\nErrorRecoveryGoal goal\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  actionlib_msgs.GoalID getGoalId();
  void setGoalId(actionlib_msgs.GoalID value);
  franka_control.ErrorRecoveryGoal getGoal();
  void setGoal(franka_control.ErrorRecoveryGoal value);
}
