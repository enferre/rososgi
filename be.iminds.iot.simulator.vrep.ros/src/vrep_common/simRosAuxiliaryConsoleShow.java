package vrep_common;

public interface simRosAuxiliaryConsoleShow extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleShow";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsoleShow(simInt consoleHandle,simBool showState)\n#\n\nint32 consoleHandle\nuint8 showState\n---\nint32 result\n\n";
}
