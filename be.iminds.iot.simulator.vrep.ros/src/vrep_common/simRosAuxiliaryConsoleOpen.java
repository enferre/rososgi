package vrep_common;

public interface simRosAuxiliaryConsoleOpen extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleOpen";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsoleOpen(const simChar* title,simInt maxLines,simInt mode,simInt* position,simInt* size,simFloat* textColor,simFloat* backgroundColor)\n#\n\nstring title\nint32 maxLines\nint32 mode\nint32[] position\nint32[] size\nfloat32[] textColor\nfloat32[] backgroundColor\n---\nint32 consoleHandle\n\n";
}
