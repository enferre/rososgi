package vrep_common;

public interface simRosGetArrayParameter extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetArrayParameter";
  static final java.lang.String _DEFINITION = "#\n# simInt simGetArrayParameter(simInt parameter,simVoid* parameterValues)\n#\n\nint32 parameter\n---\nint32 result\nfloat32[] parameterValues\n";
}
