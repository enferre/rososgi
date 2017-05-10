package be.iminds.iot.robot.api.arm;

public class JointValue {

	public enum Type {
		POSITION,
		VELOCITY,
		TORQUE
	}
	
	public String joint;
	public Type type;
	public float value;
	
	public JointValue(String joint, Type t, float val){
		this.joint = joint;
		this.type = t;
		this.value = val;
	}
}
