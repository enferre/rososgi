package be.iminds.iot.robot.api;

public class Position {

	public float x;
	public float y;
	public float z;
	
	public Position() {}
	
	public Position(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return x+" "+y+" "+z;
	}
}
