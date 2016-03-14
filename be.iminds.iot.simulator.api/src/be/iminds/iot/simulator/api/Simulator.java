package be.iminds.iot.simulator.api;

public interface Simulator {

	void start();
	
	void pause();
	
	void stop();
	
	// TODO tick()
	
	void loadScene(String file);
	
}
