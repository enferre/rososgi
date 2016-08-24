package be.iminds.iot.simulator.api;

public class Orientation {

	public float alfa;
	public float beta;
	public float gamma;
	
	public Orientation(){}
	
	public Orientation(float alfa, float beta, float gamma){
		this.alfa = alfa;
		this.beta = beta;
		this.gamma = gamma;
	}
	
	public String toString(){
		return "["+alfa+","+beta+","+gamma+"]";
	}
}
