package be.iminds.iot.robot.api;

public class JointDescription {

	public final String name;
	
	public final float positionMin;
	public final float positionMax;
	
	public final float velocityMin;
	public final float velocityMax;
	
	public final float torqueMin;
	public final float torqueMax;
	
	public JointDescription(String name,
			float pMin, float pMax,
			float vMin, float vMax,
			float tMin, float tMax){
		
		this.name = name;
		
		this.positionMin = pMin;
		this.positionMax = pMax;
		
		this.velocityMin = vMin;
		this.velocityMax = vMax;
		
		this.torqueMin = tMin;
		this.torqueMax = tMax;
	}
	
	public String getName(){
		return name;
	}
	
	public float getPositionMin(){
		return positionMin;
	}
	
	public float getPositionMax(){
		return positionMax;
	}
	
	public float getVelocityMin(){
		return velocityMin;
	}
	
	public float getVelocityMax(){
		return velocityMax;
	}
	
	public float getTorqueMin(){
		return torqueMin;
	}
	
	public float getTorqueMax(){
		return torqueMax;
	}
}
