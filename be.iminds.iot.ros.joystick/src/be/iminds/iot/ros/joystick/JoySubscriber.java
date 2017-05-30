/*******************************************************************************
 *  ROSOSGi - Bridging the gap between Robot Operating System (ROS) and OSGi
 *  Copyright (C) 2015, 2016  imec - IDLab - UGent
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
package be.iminds.iot.ros.joystick;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
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

import be.iminds.iot.input.joystick.api.JoystickEvent;
import be.iminds.iot.input.joystick.api.JoystickListener;
import be.iminds.iot.input.joystick.api.JoystickEvent.JoystickButton;
import be.iminds.iot.input.joystick.api.JoystickEvent.Type;
import sensor_msgs.Joy;

@Component(service = NodeMain.class)
public class JoySubscriber extends AbstractNodeMain {

	private volatile List<JoystickListener> listeners = new ArrayList<>();
	
	private Subscriber<Joy> subscriber;

	private int[] previousButtons = new int[16];
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("joy/subscriber");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		String topic = "/joy";
		subscriber = connectedNode.newSubscriber(topic,
				Joy._TYPE);
		subscriber.addMessageListener(new MessageListener<Joy>() {
			@Override
			public void onNewMessage(Joy joy) {
				float[] axes = joy.getAxes();
				int[] buttons = joy.getButtons();
				Type type = Type.JOYSTICK_CHANGED;
				
				for(int i=0;i<JoystickButton.values().length;i++){
					if(previousButtons[i]==0 && buttons[i] == 1){
						// button press event
						type = Type.values()[i];
					} else if(previousButtons[i]==1 && buttons[i] == 0) {
						// button release event
						type = Type.values()[i+JoystickButton.values().length];
					}
				}
				
				JoystickEvent e = new JoystickEvent(type, axes, buttons);
				for(JoystickListener l : listeners){
					l.onEvent(e);
				}
				
				previousButtons = buttons;
			}
		});
	}

	@Override
	public void onShutdown(Node node) {
		subscriber.shutdown();
	}

	@Reference(cardinality=ReferenceCardinality.MULTIPLE, policy=ReferencePolicy.DYNAMIC)
	synchronized void addJoystickListener(JoystickListener l, Map<String, Object> properties){
		List<JoystickListener> copy = new ArrayList<>(listeners);
		copy.add(l);
		listeners = copy;
	}
	
	synchronized void removeJoystickListener(JoystickListener l, Map<String, Object> properties){
		List<JoystickListener> copy = new ArrayList<>(listeners);
		copy.remove(l);
		listeners = copy;
	}
}
