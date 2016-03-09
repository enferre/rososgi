package be.iminds.iot.ros.simulator.api;

public interface Simulator {

	void start();
	
	void pause();
	
	void stop();
	
	void loadScene(String file);
	
}
