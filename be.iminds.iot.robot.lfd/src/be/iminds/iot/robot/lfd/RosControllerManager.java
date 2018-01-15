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
package be.iminds.iot.robot.lfd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.promise.Deferred;
import org.ros.exception.RemoteException;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.service.ServiceClient;
import org.ros.node.service.ServiceResponseListener;

import controller_manager_msgs.LoadControllerRequest;
import controller_manager_msgs.LoadControllerResponse;
import controller_manager_msgs.SwitchControllerRequest;
import controller_manager_msgs.SwitchControllerResponse;
import controller_manager_msgs.UnloadControllerRequest;
import controller_manager_msgs.UnloadControllerResponse;

@Component(service = {NodeMain.class, ControllerManager.class},
		immediate=true)
public class RosControllerManager extends AbstractNodeMain implements ControllerManager {

	private String name;
	
	private ServiceClient<controller_manager_msgs.LoadControllerRequest, controller_manager_msgs.LoadControllerResponse> load;
	private ServiceClient<controller_manager_msgs.UnloadControllerRequest, controller_manager_msgs.UnloadControllerResponse> unload;
	private ServiceClient<controller_manager_msgs.SwitchControllerRequest, controller_manager_msgs.SwitchControllerResponse> swtch;
	
	@Activate
	void activate(BundleContext context, Map<String, Object> config){
		if(config.containsKey("name")) {
			name = config.get("name").toString();
		} else {
			name = "lfd";
		}
	}
	
	@Deactivate
	void deactivate(){
		
	}
	
	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of(name.toLowerCase()+"/control_manager");
	}
	
	@Override
	public void onStart(ConnectedNode connectedNode){
		// TODO make this configurable?!
		try {
			load = connectedNode.newServiceClient("/panda/controller_manager/load_controller", controller_manager_msgs.LoadController._TYPE);
			unload = connectedNode.newServiceClient("/panda/controller_manager/unload_controller", controller_manager_msgs.UnloadController._TYPE);
			swtch = connectedNode.newServiceClient("/panda/controller_manager/switch_controller", controller_manager_msgs.SwitchController._TYPE);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void load(String controller) {
		Deferred<Void> deferred = new Deferred<>();
		LoadControllerRequest rq = load.newMessage();
		rq.setName(controller);
		load.call(rq, new ServiceResponseListener<LoadControllerResponse>() {
			
			@Override
			public void onSuccess(LoadControllerResponse resp) {
				if(resp.getOk()) {
					deferred.resolve(null);
				} else {
					deferred.fail(new Exception("Failed to load controller "+controller));
				}
			}
			
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void unload(String controller) {
		Deferred<Void> deferred = new Deferred<>();
		UnloadControllerRequest rq = unload.newMessage();
		rq.setName(controller);
		unload.call(rq, new ServiceResponseListener<UnloadControllerResponse>() {
			
			@Override
			public void onSuccess(UnloadControllerResponse resp) {
				if(resp.getOk()) {
					deferred.resolve(null);
				} else {
					deferred.fail(new Exception("Failed to load controller "+controller));
				}
			}
			
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void start(String controller) {
		Deferred<Void> deferred = new Deferred<>();
		SwitchControllerRequest rq = swtch.newMessage();
		List<String> toStart = new ArrayList<>();
		toStart.add(controller);
		rq.setStartControllers(toStart);
		rq.setStrictness(1);
		swtch.call(rq, new ServiceResponseListener<SwitchControllerResponse>() {
			
			@Override
			public void onSuccess(SwitchControllerResponse resp) {
				if(resp.getOk()) {
					deferred.resolve(null);
				} else {
					deferred.fail(new Exception("Failed to load controller "+controller));
				}
			}
			
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void stop(String controller) {
		Deferred<Void> deferred = new Deferred<>();
		SwitchControllerRequest rq = swtch.newMessage();
		List<String> toStop = new ArrayList<>();
		toStop.add(controller);
		rq.setStopControllers(toStop);
		rq.setStrictness(1);
		swtch.call(rq, new ServiceResponseListener<SwitchControllerResponse>() {
			
			@Override
			public void onSuccess(SwitchControllerResponse resp) {
				if(resp.getOk()) {
					deferred.resolve(null);
				} else {
					deferred.fail(new Exception("Failed to load controller "+controller));
				}
			}
			
			@Override
			public void onFailure(RemoteException ex) {
				deferred.fail(ex);
			}
		});
		try {
			deferred.getPromise().getValue();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
