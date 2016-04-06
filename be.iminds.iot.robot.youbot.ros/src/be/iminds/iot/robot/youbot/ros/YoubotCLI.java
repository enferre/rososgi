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
	"osgi.command.function=position",
	"osgi.command.function=velocity",
	"osgi.command.function=torque",
	"osgi.command.function=move",
	"osgi.command.function=halt"})
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
	
	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
	@Reference
	void setBase(OmniDirectional base){
		this.base = base;
	}
}
