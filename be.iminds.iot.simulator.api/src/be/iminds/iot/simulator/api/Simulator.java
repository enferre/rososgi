package be.iminds.iot.simulator.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public interface Simulator {

	// start/stop/pause simulator
	
	void start(boolean sync);
	
	void pause();
	
	void stop();
	
	void tick() throws TimeoutException;
	
	// load scene
	default void loadScene(String file){
		loadScene(file, new HashMap<String, String>());
	}
	
	void loadScene(String file, Map<String, String> entities);
	
	// get and set positions of objects
	
	Position getPosition(String object);
	
	void setPosition(String object, Position p);
	
	Position getPosition(String object, String relativeTo);
	
	void setPosition(String object, String relativeTo, Position p);
	
	// get and set orientation of objects
	
	Orientation getOrientation(String object);
	
	void setOrientation(String object, Orientation o);
	
	Orientation getOrientation(String object, String relativeTo);
	
	void setOrientation(String object, String relativeTo, Orientation o);
	
	// check collisions
	boolean checkCollisions(String object);
	
}
