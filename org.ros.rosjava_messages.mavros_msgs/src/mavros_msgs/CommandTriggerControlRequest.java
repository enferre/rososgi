package mavros_msgs;

public interface CommandTriggerControlRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/CommandTriggerControlRequest";
  static final java.lang.String _DEFINITION = "# Type for controlling onboard camera trigerring system\n\nbool    trigger_enable\t\t# Trigger on/off control\nfloat32 integration_time\t# Shutter integration time. Zero to use current onboard value.\n";
  boolean getTriggerEnable();
  void setTriggerEnable(boolean value);
  float getIntegrationTime();
  void setIntegrationTime(float value);
}
