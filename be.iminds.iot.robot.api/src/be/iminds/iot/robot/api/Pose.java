package be.iminds.iot.robot.api;

public class Pose {

	public Position position;
	public Orientation orientation;
	
	public Pose() {
		this.position = new Position();
		this.orientation = new Orientation();
	}
	
	public Pose(Position p, Orientation o) {
		this.position = p;
		this.orientation = o;
	}
	
	public Pose(float x, float y, float z, float ox, float oy, float oz, float ow) {
		this.position = new Position(x,y,z);
		this.orientation = new Orientation(ox,oy,oz,ow);
	}
	
	@Override
	public String toString() {
		return position.toString()+","+orientation.toString();
		
	}
}
