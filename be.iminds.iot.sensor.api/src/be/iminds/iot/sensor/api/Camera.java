package be.iminds.iot.sensor.api;

public interface Camera extends Sensor {

	int getWidth();
	
	int getHeight();
	
	String getEncoding();
	
}
