package mavros_msgs;

public interface State extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/State";
  static final java.lang.String _DEFINITION = "# Current autopilot state\n#\n# Known modes listed here:\n# http://wiki.ros.org/mavros/CustomModes\n\nstd_msgs/Header header\nbool connected\nbool armed\nbool guided\nstring mode\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  boolean getConnected();
  void setConnected(boolean value);
  boolean getArmed();
  void setArmed(boolean value);
  boolean getGuided();
  void setGuided(boolean value);
  java.lang.String getMode();
  void setMode(java.lang.String value);
}
