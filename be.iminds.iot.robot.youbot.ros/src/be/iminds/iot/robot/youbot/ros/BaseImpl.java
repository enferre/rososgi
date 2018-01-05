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
package be.iminds.iot.robot.youbot.ros;

import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.omni.OmniDirectional;
import geometry_msgs.Twist;

public class BaseImpl implements OmniDirectional {

	private final String name;
	private final BundleContext context;
	private ServiceRegistration<OmniDirectional> registration;
	
	private final ConnectedNode node;
	
	private Publisher<geometry_msgs.Twist> pTwist;
	
	private Deferred<OmniDirectional> deferred = null;
	private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	
	public BaseImpl(String name, BundleContext context,
			ConnectedNode node){
		this.name = name;
		this.context = context;
		this.node = node;
		
		// TODO separate service for wheel joints?
		// can you even control those individually using ROS interface?
		
		// TODO expose odometry information?
		
	}
	
	public void register(){
		pTwist = node.newPublisher("/cmd_vel", geometry_msgs.Twist._TYPE);
		
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put("name", name);
		registration = 	context.registerService(OmniDirectional.class, this, properties);
	}
	
	public void unregister(){
		if(registration != null){
			registration.unregister();
		}
		
		pTwist.shutdown();
	}

	@Override
	public Promise<OmniDirectional> setVelocities(Collection<JointValue> velocities) {
		// TODO is this even possible with current ROS interface?
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized Promise<OmniDirectional> move(float vx, float vy, float va) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
			deferred = null;
		}
		// will be resolved immediately
		Deferred<OmniDirectional> d = new Deferred<>();

		Twist cmd = pTwist.newMessage();
		
		cmd.getLinear().setX(vx);
		cmd.getLinear().setY(vy);
		cmd.getLinear().setZ(0);
		
		cmd.getAngular().setX(0);
		cmd.getAngular().setY(0);
		cmd.getAngular().setZ(va);

		pTwist.publish(cmd);
		
		// resolve immediately
		d.resolve(BaseImpl.this);
		return d.getPromise();
	}
	
	@Override
	public synchronized Promise<OmniDirectional> waitFor(long time) {
		if(deferred!=null){
			deferred.fail(new Exception("Operation interrupted!"));
		}
		deferred = new Deferred<OmniDirectional>();

		executor.schedule(new ResolveTask(deferred), time, TimeUnit.MILLISECONDS);
	
		return deferred.getPromise();
	}

	@Override
	public Promise<OmniDirectional> stop() {
		return move(0, 0, 0);
	}
	
	private class ResolveTask implements Runnable {
		
		private Deferred<OmniDirectional> deferred;
		
		public ResolveTask(Deferred<OmniDirectional> deferred){
			this.deferred = deferred;
		}
		
		@Override
		public void run() {
			if(deferred == BaseImpl.this.deferred){
				synchronized(BaseImpl.this){
					BaseImpl.this.deferred = null;
				}
			}
				
			try {
				deferred.resolve(BaseImpl.this);
			} catch(IllegalStateException e){
				// ignore if already resolved
			}
		}
	}
}
