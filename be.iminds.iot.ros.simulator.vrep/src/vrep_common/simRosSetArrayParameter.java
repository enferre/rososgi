package vrep_common;

public interface simRosSetArrayParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetArrayParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetArrayParameter(simInt parameter,simVoid* parameterValues)\n#\n\nint32 parameter\nfloat32[] parameterValues\n---\nint32 result\n";
}
