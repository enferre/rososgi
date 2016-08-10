package be.iminds.iot.simulator.api;

public class Position {

	public double x;
	public double y;
	public double z;
	
	public Position(){}
	
	public Position(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toString(){
		return "["+x+","+y+","+z+"]";
	}
}
