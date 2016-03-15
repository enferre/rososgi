package vrep_common;

public interface simRosSynchronousRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSynchronousRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxSynchronous(simxChar enable)\n#\n\nuint8 enable\n";
  byte getEnable();
  void setEnable(byte value);
}
