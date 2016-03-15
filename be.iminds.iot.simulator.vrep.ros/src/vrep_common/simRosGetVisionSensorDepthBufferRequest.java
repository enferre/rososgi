package vrep_common;

public interface simRosGetVisionSensorDepthBufferRequest extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "vrep_common/simRosGetVisionSensorDepthBufferRequest";
  static final java.lang.String _DEFINITION = "#\n# simxInt simxGetVisionSensorDepthBuffer(simxInt sensorHandle,simxInt* resolution,simxFloat** buffer,simxInt operationMode)\n#\n\nint32 handle\n";
  int getHandle();
  void setHandle(int value);
}
