package vrep_common;

public interface simRosAuxiliaryConsoleOpenResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleOpenResponse";
  static final java.lang.String _DEFINITION = "int32 consoleHandle";
  int getConsoleHandle();
  void setConsoleHandle(int value);
}
