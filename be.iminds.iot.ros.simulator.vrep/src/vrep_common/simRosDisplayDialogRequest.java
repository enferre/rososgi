package vrep_common;

public interface simRosDisplayDialogRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisplayDialogRequest";
  static final java.lang.String _DEFINITION = "#\n# simInt simDisplayDialog(const simChar* titleText,const simChar* mainText,simInt dialogType,const simChar* initialText,const simFloat* titleColors,const simFloat* dialogColors,simInt* uiHandle)\n#\n\nstring titleText\nstring mainText\nint32 dialogType\nstring initialText\nfloat32[] titleColors\nfloat32[] dialogColors\n";
  java.lang.String getTitleText();
  void setTitleText(java.lang.String value);
  java.lang.String getMainText();
  void setMainText(java.lang.String value);
  int getDialogType();
  void setDialogType(int value);
  java.lang.String getInitialText();
  void setInitialText(java.lang.String value);
  float[] getTitleColors();
  void setTitleColors(float[] value);
  float[] getDialogColors();
  void setDialogColors(float[] value);
}
