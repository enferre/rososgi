package vrep_common;

public interface simRosSetObjectParentRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectParentRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectParent(simInt objectHandle,simInt parentObjectHandle,simBool keepInPlace)\n#\n\nint32 handle\nint32 parentHandle\nuint8 keepInPlace\n";
  int getHandle();
  void setHandle(int value);
  int getParentHandle();
  void setParentHandle(int value);
  byte getKeepInPlace();
  void setKeepInPlace(byte value);
}
