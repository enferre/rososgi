package vrep_common;

public interface simRosGetVisionSensorDepthBuffer extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorDepthBuffer";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetVisionSensorDepthBuffer(simxInt sensorHandle,simxInt* resolution,simxFloat** buffer,simxInt operationMode)\n#\n\nint32 handle\n---\nint32 result\nint32[] resolution\nfloat32[] buffer\n";
}
