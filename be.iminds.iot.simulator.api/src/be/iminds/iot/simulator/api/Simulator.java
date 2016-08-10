package be.iminds.iot.simulator.api;

public interface Simulator {

	// start/stop/pause simulator
	
	void start(boolean sync);
	
	void pause();
	
	void stop();
	
	void tick();
	
	// load scene
	
	void loadScene(String file);
	
	// get and set positions of objects
	
	Position getPosition(String object);
	
	void setPosition(String object, Position p);
	
	Position getPosition(String object, String relativeTo);
	
	void setPosition(String object, String relativeTo, Position p);
}
