package be.iminds.iot.simulator.api;

public interface Simulator {

	void start(boolean sync);
	
	void pause();
	
	void stop();
	
	void tick();
	
	void loadScene(String file);
	
}
