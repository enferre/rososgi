package object_recognition_msgs;

public interface ObjectRecognitionGoal extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "object_recognition_msgs/ObjectRecognitionGoal";
  static final java.lang.String _DEFINITION = "# ====== DO NOT MODIFY! AUTOGENERATED FROM AN ACTION DEFINITION ======\n# Optional ROI to use for the object detection\nbool use_roi\nfloat32[] filter_limits\n";
  boolean getUseRoi();
  void setUseRoi(boolean value);
  float[] getFilterLimits();
  void setFilterLimits(float[] value);
}