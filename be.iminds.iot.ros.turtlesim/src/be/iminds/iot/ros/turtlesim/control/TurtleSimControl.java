package be.iminds.iot.ros.turtlesim.control;

import org.osgi.service.component.annotations.Component;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;

import geometry_msgs.Twist;

@Component(service = NodeMain.class,
	property = {"osgi.command.scope=turtle", 
		"osgi.command.function=circle",
		"osgi.command.function=halt"})
public class TurtleSimControl extends AbstractNodeMain {

	private Publisher<geometry_msgs.Twist> publisher;
	
	private volatile boolean running;
	private Thread t;
	
	private float a_x = 0;
	private float a_y = 0;
	private float a_z = 0;
	
	private float l_x = 0;
	private float l_y = 0;
	private float l_z = 0;
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("turtlesim_control");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		publisher = connectedNode.newPublisher("turtle1/cmd_vel", geometry_msgs.Twist._TYPE);
	}
	
	void run(){
		while(running){
			if(publisher!=null){
				Twist cmd = publisher.newMessage();
				
				cmd.getLinear().setX(l_x);
				cmd.getLinear().setY(l_y);
				cmd.getLinear().setZ(l_z);
				
				cmd.getAngular().setX(a_x);
				cmd.getAngular().setY(a_y);
				cmd.getAngular().setZ(a_z);

				publisher.publish(cmd);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
	
	void start(){
		if(!running){
			running = true;
			t = new Thread(()->run());
			t.start();
		}
	}
	
	public void circle(){
		l_x = 1;
		l_y = 0;
		l_z = 0;
		
		a_x = 0;
		a_y = 0;
		a_z = 1;
		
		start();
	}
	
	public void halt(){
		if(running){
			running = false;
		}
		if(t!=null){
			try {
				t.join();
			} catch (InterruptedException e) {
			}
		}
	}
}
