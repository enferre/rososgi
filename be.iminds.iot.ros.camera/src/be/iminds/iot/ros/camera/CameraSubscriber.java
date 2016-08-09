package be.iminds.iot.ros.camera;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

import be.iminds.iot.sensor.api.Camera;
import be.iminds.iot.sensor.api.Frame;
import be.iminds.iot.sensor.api.SensorListener;
import be.iminds.iot.sensor.api.SensorValue;
import sensor_msgs.Image;

/**
 * Subscribes to the image_raw topic that a camera node is publishing to and exposes this to OSGi
 * 
 * @author tverbele
 *
 */
@Component(service = NodeMain.class,
	name="be.iminds.iot.ros.camera.Camera",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class CameraSubscriber extends AbstractNodeMain implements Camera {

	private BundleContext context;
	private ServiceRegistration<Camera> registration;
	
	private volatile List<SensorListener> listeners = new ArrayList<>();

	private String name;
	private UUID id = UUID.randomUUID();
	private volatile Frame currentFrame = null;
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("camera_subscriber");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		Subscriber<Image> subscriber = connectedNode.newSubscriber(name+"/image_raw",
				Image._TYPE);
		subscriber.addMessageListener(new MessageListener<Image>() {
			@Override
			public void onNewMessage(Image image) {

				Frame f = new Frame();
				f.width = image.getWidth();
				f.height = image.getHeight();
				f.encoding = image.getEncoding();
				
				// TODO convert to float array for all supported encodings
				switch(f.encoding){
					case "rgb8":
						f.data = new float[f.width*f.height*3];
						byte[] data = image.getData().array();
						
						int ind = 0;
						for(int j=0;j<f.height;j++){
							for(int i=0;i<f.width;i++){
								for(int k=0;k<3;k++){
									f.data[k*f.width*f.height+j*f.height+i] =
											(0xFF & data[ind++])/255.f;
								}
							}
						}
						
						break;
					default: 
						System.out.println("Encoding "+f.encoding+" not supported?!");
				}
				
				if(currentFrame == null){
					// register Camera service
					Dictionary<String, Object> properties = new Hashtable<>();
					properties.put("id", id.toString());
					registration = context.registerService(Camera.class, CameraSubscriber.this, properties);
				}
				currentFrame = f;
				
				for(SensorListener l : listeners){
					l.update(currentFrame);
				}
			}
		});
	}


	@Override
	public void onShutdown(Node node) {
		if(registration != null){
			registration.unregister();
			currentFrame = null;
		}
	}
	
	@Override
	public SensorValue getValue() {
		return currentFrame;
	}

	@Override
	public int getWidth() {
		return currentFrame.width;
	}

	@Override
	public int getHeight() {
		return currentFrame.height;
	}

	@Override
	public String getEncoding() {
		return currentFrame.encoding;
	}
	
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC)
	synchronized void addSensorListener(SensorListener l, Map<String, Object> properties){
		String[] tt = new String[0];
		Object target = properties.get("target");
		if(target instanceof String){
			String t = (String)target;
			if(t.contains(",")){
				tt = t.split(",");
			} else {
				tt = new String[]{t};
			}
		} else if( target instanceof String[]){
			tt = (String[]) target;
		}
		
		boolean filter = true;
		for(String t : tt){
			if(t.equals("*") || t.equals(id.toString())){
				filter = false;
				break;
			}
		}
		
		if(!filter){
			List<SensorListener> copy = new ArrayList<>(listeners);
			copy.add(l);
			listeners = copy;
		}
	}
	
	synchronized void removeSensorListener(SensorListener l, Map<String, Object> properties){
		List<SensorListener> copy = new ArrayList<>(listeners);
		copy.remove(l);
		listeners = copy;
	}
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		this.context = context;
		this.name = (String)config.get("name");
	}
}
