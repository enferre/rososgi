package franka_gripper;

public interface MoveAction extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "franka_gripper/MoveAction";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n\nMoveActionGoal action_goal\nMoveActionResult action_result\nMoveActionFeedback action_feedback\n";
  franka_gripper.MoveActionGoal getActionGoal();
  void setActionGoal(franka_gripper.MoveActionGoal value);
  franka_gripper.MoveActionResult getActionResult();
  void setActionResult(franka_gripper.MoveActionResult value);
  franka_gripper.MoveActionFeedback getActionFeedback();
  void setActionFeedback(franka_gripper.MoveActionFeedback value);
}