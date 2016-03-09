package vrep_common;

public interface simRosAuxiliaryConsoleOpenRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosAuxiliaryConsoleOpenRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simAuxiliaryConsoleOpen(const simChar* title,simInt maxLines,simInt mode,simInt* position,simInt* size,simFloat* textColor,simFloat* backgroundColor)\n#\n\nstring title\nint32 maxLines\nint32 mode\nint32[] position\nint32[] size\nfloat32[] textColor\nfloat32[] backgroundColor\n";
  java.lang.String getTitle();
  void setTitle(java.lang.String value);
  int getMaxLines();
  void setMaxLines(int value);
  int getMode();
  void setMode(int value);
  int[] getPosition();
  void setPosition(int[] value);
  int[] getSize();
  void setSize(int[] value);
  float[] getTextColor();
  void setTextColor(float[] value);
  float[] getBackgroundColor();
  void setBackgroundColor(float[] value);
}
