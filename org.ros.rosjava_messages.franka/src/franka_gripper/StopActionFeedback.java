package franka_gripper;

public interface StopActionFeedback extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "franka_gripper/StopActionFeedback";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n\nHeader header\nactionlib_msgs/GoalStatus status\nStopFeedback feedback\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  actionlib_msgs.GoalStatus getStatus();
  void setStatus(actionlib_msgs.GoalStatus value);
  franka_gripper.StopFeedback getFeedback();
  void setFeedback(franka_gripper.StopFeedback value);
}
