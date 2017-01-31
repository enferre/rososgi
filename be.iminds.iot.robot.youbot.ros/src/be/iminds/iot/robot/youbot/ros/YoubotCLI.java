package be.iminds.iot.robot.youbot.ros;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.robot.api.Arm;
import be.iminds.iot.robot.api.OmniDirectional;

@Component(service={Object.class},
	property = {"osgi.command.scope=youbot", 
	"osgi.command.function=armTest",
	"osgi.command.function=baseTest",
	"osgi.command.function=reset",
	"osgi.command.function=candle",
	"osgi.command.function=grippose",
	"osgi.command.function=grip",
	"osgi.command.function=invgrip",
	"osgi.command.function=open",
	"osgi.command.function=close",
	"osgi.command.function=position",
	"osgi.command.function=velocity",
	"osgi.command.function=torque",
	"osgi.command.function=move",
	"osgi.command.function=halt",
	"osgi.command.function=arm"})
public class YoubotCLI {

	private Arm arm;
	private OmniDirectional base;
	
	public void armTest(){
		// preprogrammed arm movement
		arm.openGripper(0.02f)
			.then(p -> arm.setPositions(0.7f, 0.1f, -3.5f, 1.0f, 1.5f))
			.then(p -> arm.waitFor(2000))
			.then(p -> arm.setPosition(0, 0.011f))
			.then(p -> arm.closeGripper());
	}
	
	public void baseTest(){
		// preprogrammed base movement
		base.move(0, 0, 3)
			.then(p -> base.waitFor(5000))
			.then(p -> base.move(2, 0, 0))
			.then(p -> base.waitFor(5000))
			.then(p -> base.move(0, 0, 3))
			.then(p -> base.waitFor(5000))
			.then(p -> base.stop());
	}
	
	public void reset(){
		arm.setPositions(0.0100693f, 0.0100693f, -0.015708f, 0.0221239f, 0.11062f);
	}
	
	public void candle(){
		arm.setPositions(2.92510465f, 1.103709733f, -2.478948503f, 1.72566195f);
	}

	public void grippose(){
		arm.openGripper()
			.then(p -> arm.setPosition(0, 2.92f))
			.then(p -> arm.setPosition(4, 2.875f))
			.then(p -> arm.setPositions(2.92f, 1.76f, -1.37f, 2.55f));
	}
	
	public void grip(){
		arm.openGripper()
			.then(p -> arm.setPosition(0, 2.92f))
			.then(p -> arm.setPosition(4, 2.875f))
			.then(p -> arm.setPositions(2.92f, 1.76f, -1.37f, 2.55f))
			.then(p -> arm.closeGripper())
			.then(p -> arm.setPositions(0.01f, 0.8f))
			.then(p -> arm.setPositions(0.01f, 0.8f, -1f, 2.9f))
			.then(p -> arm.openGripper())
			.then(p -> arm.setPosition(1, -1.3f))
			.then(p -> arm.reset());
	}
	
	public void invgrip(){
		arm.openGripper()
			.then(p -> arm.setPosition(4, 2.875f))
			.then(p -> arm.setPositions(0.01f, -1.3f, -1.0f, 2.9f))
			.then(p -> arm.setPosition(1, 0.8f))
			.then(p -> arm.closeGripper())
			.then(p -> arm.setPosition(1, 0.5f))
			.then(p -> arm.setPositions(2.92f, 0.8f, -1.37f, 2.55f))
			.then(p -> arm.setPosition(1, 1.76f))
			.then(p -> arm.openGripper())
			.then(p -> arm.setPosition(1, 1.4f))
			.then(p -> arm.reset());
	}
	
	public void arm(){
		arm.getState().stream().forEach(joint -> System.out.println(joint.joint+" - pos: "+joint.position+" vel: "+joint.velocity+" torque: "+joint.torque));
	}

	// positions as index,position pairs
	public void position(String positions){
		String[] pairs = positions.split(" ");
		for(String pair : pairs){
			String[] kv = pair.split(",");
			int joint = Integer.parseInt(kv[0]);
			float val = Float.parseFloat(kv[1]);
			arm.setPosition(joint, val);
		}
	}
	
	public void position(float... positions){
		arm.setPositions(positions);
	}
	
	public void position(int joint, float val){
		arm.setPosition(joint, val);
	}
	
	public void velocity(int joint, float val){
		arm.setVelocity(joint, val);
	}
	
	public void torque(int joint, float val){
		arm.setTorque(joint, val);
	}
	
	public void move(float vx, float vy, float va){
		base.move(vx, vy, va);
	}
	
	public void halt(){
		base.stop();
		arm.stop();
	}
	
	public void open(float opening){
		arm.openGripper(opening);
	}
	
	public void open(){
		arm.openGripper();
	}
	
	public void close(){
		arm.closeGripper();
	}
	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
	@Reference
	void setBase(OmniDirectional base){
		this.base = base;
	}
}
