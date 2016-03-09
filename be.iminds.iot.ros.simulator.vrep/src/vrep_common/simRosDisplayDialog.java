package vrep_common;

public interface simRosDisplayDialog extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosDisplayDialog";
  static final java.lang.String _DEFINITION = "#\n# simInt simDisplayDialog(const simChar* titleText,const simChar* mainText,simInt dialogType,const simChar* initialText,const simFloat* titleColors,const simFloat* dialogColors,simInt* uiHandle)\n#\n\nstring titleText\nstring mainText\nint32 dialogType\nstring initialText\nfloat32[] titleColors\nfloat32[] dialogColors\n---\nint32 dialogHandle\nint32 uiHandle\n";
}
