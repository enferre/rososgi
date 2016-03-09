package vrep_common;

public interface simRosAuxiliaryConsolePrintRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsolePrintRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsolePrint(simInt consoleHandle,const simChar* text)\n#\n\nint32 consoleHandle\nstring text\n";
  int getConsoleHandle();
  void setConsoleHandle(int value);
  java.lang.String getText();
  void setText(java.lang.String value);
}
