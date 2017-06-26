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
package be.iminds.iot.ros.core;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.ros.concurrent.DefaultScheduledExecutorService;
import org.ros.node.DefaultNodeMainExecutor;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMain;
import org.ros.node.NodeMainExecutor;

import be.iminds.iot.ros.api.Environment;

@Component
public class RosRuntime {

	private NodeMainExecutor executor;
	private ThreadPoolExecutor pool;
	private int threadCount = 0;
	
	private Environment env;
	
	public RosRuntime(){
		// these parameters are equal as for CachedThreadPool ... change if useful
		pool = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, 
				new SynchronousQueue<Runnable>(), new ThreadFactory() {	
					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "rosjava-pool-"+(threadCount++));
					}
				});
		executor = DefaultNodeMainExecutor.newDefault(new DefaultScheduledExecutorService(pool));
	}
	

	@Deactivate
	void deactivate(){
		executor.shutdown();
	}
	
	@Reference
	void setEnvironment(Environment e){
		this.env = e;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, 
			   policy=ReferencePolicy.DYNAMIC)
	void addNode(NodeMain node) {
		try {
			NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic(env.getHost(), env.getMasterURI());
			nodeConfiguration.setRosRoot(env.getRoot());
			nodeConfiguration.setRosPackagePath(env.getPackagePath());
			executor.execute(node, nodeConfiguration);
		} catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	void removeNode(NodeMain node){
		try {
			executor.shutdownNodeMain(node);
		} catch(Throwable t){
			t.printStackTrace();
		}
	}
	
}
