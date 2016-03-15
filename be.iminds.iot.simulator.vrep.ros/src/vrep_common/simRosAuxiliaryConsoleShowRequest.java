package vrep_common;

public interface simRosAuxiliaryConsoleShowRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleShowRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsoleShow(simInt consoleHandle,simBool showState)\n#\n\nint32 consoleHandle\nuint8 showState\n";
  int getConsoleHandle();
  void setConsoleHandle(int value);
  byte getShowState();
  void setShowState(byte value);
}
