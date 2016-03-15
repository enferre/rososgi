package vrep_common;

public interface simRosAuxiliaryConsoleCloseRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleCloseRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsoleClose(simInt consoleHandle)\n#\n\nint32 consoleHandle\n";
  int getConsoleHandle();
  void setConsoleHandle(int value);
}
