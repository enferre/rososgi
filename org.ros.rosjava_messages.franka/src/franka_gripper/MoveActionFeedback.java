package franka_gripper;

public interface MoveActionFeedback extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "franka_gripper/MoveActionFeedback";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n\nHeader header\nactionlib_msgs/GoalStatus status\nMoveFeedback feedback\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  actionlib_msgs.GoalStatus getStatus();
  void setStatus(actionlib_msgs.GoalStatus value);
  franka_gripper.MoveFeedback getFeedback();
  void setFeedback(franka_gripper.MoveFeedback value);
}