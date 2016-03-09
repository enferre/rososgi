package vrep_common;

public interface simRosSetObjectParent extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosSetObjectParent";
  static final java.lang.String _DEFINITION = "#\n# simInt simSetObjectParent(simInt objectHandle,simInt parentObjectHandle,simBool keepInPlace)\n#\n\nint32 handle\nint32 parentHandle\nuint8 keepInPlace\n---\nint32 result\n";
}
