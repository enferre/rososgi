package vrep_common;

public interface simRosAuxiliaryConsolePrint extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsolePrint";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsolePrint(simInt consoleHandle,const simChar* text)\n#\n\nint32 consoleHandle\nstring text\n---\nint32 result\n\n";
}
