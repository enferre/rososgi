package vrep_common;

public interface simRosGetUIEventButtonResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetUIEventButtonResponse";
  static final java.lang.String _DEFINITION = "int32 buttonID\nint32[] auxiliaryValues";
  int getButtonID();
  void setButtonID(int value);
  int[] getAuxiliaryValues();
  void setAuxiliaryValues(int[] value);
}
