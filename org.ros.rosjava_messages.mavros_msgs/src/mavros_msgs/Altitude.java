package mavros_msgs;

public interface Altitude extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "mavros_msgs/Altitude";
  static final java.lang.String _DEFINITION = "# Altitude information\n#\n# https://pixhawk.ethz.ch/mavlink/#ALTITUDE\n\nstd_msgs/Header header\n\nfloat32 monotonic\nfloat32 amsl\nfloat32 local\nfloat32 relative\nfloat32 terrain\nfloat32 bottom_clearance\n";
  std_msgs.Header getHeader();
  void setHeader(std_msgs.Header value);
  float getMonotonic();
  void setMonotonic(float value);
  float getAmsl();
  void setAmsl(float value);
  float getLocal();
  void setLocal(float value);
  float getRelative();
  void setRelative(float value);
  float getTerrain();
  void setTerrain(float value);
  float getBottomClearance();
  void setBottomClearance(float value);
}
