package be.iminds.iot.robot.api;

public class Orientation {

	public float x;
	public float y;
	public float z;
	public float w;

	public Orientation(){}
	
	public Orientation(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
	@Override
	public String toString() {
		return "["+x+","+y+","+z+","+w+"]";
	}
}
