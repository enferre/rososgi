package be.iminds.iot.robot.erlerover.ros;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.robot.api.rover.Rover;

@Component(service={Object.class},
	property = {"osgi.command.scope=rover", 
	"osgi.command.function=move",
	"osgi.command.function=halt"})
public class RoverCLI {

	private Rover rover;
	
	public void move(float throttle, float yaw){
		rover.move(throttle, yaw);
	}
	
	public void halt(){
		rover.stop();
	}
	
	@Reference
	public void setRover(Rover r){
		this.rover = r;
	}
}
