package vrep_common;

public interface simRosGetUISliderResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUISliderResponse";
  static final java.lang.String _DEFINITION = "int32 position";
  int getPosition();
  void setPosition(int value);
}
