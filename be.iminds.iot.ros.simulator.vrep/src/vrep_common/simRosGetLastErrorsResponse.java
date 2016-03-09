package vrep_common;

public interface simRosGetLastErrorsResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetLastErrorsResponse";
  static final java.lang.String _DEFINITION = "int32 errorCnt\nstring errorStrings";
  int getErrorCnt();
  void setErrorCnt(int value);
  java.lang.String getErrorStrings();
  void setErrorStrings(java.lang.String value);
}
