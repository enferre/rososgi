/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2017  imec - IDLab - UGent
 *
 *  This file is part of DIANNE  -  Framework for distributed artificial neural networks
 *
 *  DIANNE is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *   
 *  Contributors:
 *      Tim Verbelen, Steven Bohez
 *******************************************************************************/
package be.iminds.iot.sensor.camera.ros;

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
	name="be.iminds.iot.sensor.camera.ros.Camera",
	configurationPolicy=ConfigurationPolicy.REQUIRE)
public class ROSCameraProvider extends AbstractNodeMain implements Camera {

	private BundleContext context;
	private ServiceRegistration<Camera> registration;
	
	private volatile List<SensorListener> listeners = new ArrayList<>();

	private String name;
	private Dictionary<String, Object> properties = new Hashtable<>();

	private UUID id = UUID.randomUUID();
	private volatile Frame currentFrame = null;
	
	private Subscriber<Image> subscriber;
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(name.replaceAll("( )|#", "_")+"/subscriber");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		subscriber = connectedNode.newSubscriber("/"+name.replaceAll("( )|#", "_").toLowerCase()+"/image_raw",
				Image._TYPE);
		subscriber.addMessageListener(new MessageListener<Image>() {
			@Override
			public void onNewMessage(Image image) {

				Frame f = new Frame();
				f.src = id;
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
									f.data[k*f.width*f.height+j*f.width+i] =
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
					registration = context.registerService(Camera.class, ROSCameraProvider.this, properties);
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
		
		subscriber.shutdown();
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
		this.properties.put("id", id.toString());
		config.entrySet().forEach(e -> this.properties.put(e.getKey(), e.getValue().toString()));
	}
}
