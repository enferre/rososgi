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
package be.iminds.iot.ros.erlerover;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

/**
 * Start APM rover process
 * 
 * TODO seems not to work ... for now launch it ourselves outside of OSGi?!
 * TODO take a look how this (w/c)ould work on the real rover
 * 
 * @author tverbele
 *
 */
@Component(immediate = true)
public class APMrover {

	private Process apmProcess;

	private String path = "/home/erle/ardupilot/APMrover2";

	@Activate
	void activate(final BundleContext context) {
		Thread t = new Thread(()->{
			try {
				ProcessBuilder builder = new ProcessBuilder("../Tools/autotest/sim_vehicle.sh", "-j", "4", "-f", "Gazebo","-v","APMrover2");
				builder.directory(new File(path));
				System.out.println("Starting APMrover process");
				apmProcess = builder.start();
	
				PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(apmProcess.getOutputStream())));
				BufferedReader reader = new BufferedReader(new InputStreamReader(apmProcess.getInputStream()));
				
				boolean done = false;
				while(!done){
					String line = reader.readLine();
					if(line.startsWith("Connect")){
						done = true;
					}
				}
				// TODO: this appears not to work from sub-process in Java?!
				writer.println("param load "+path+"/../Tools/Frame_params/3DR_Rover.param");
				writer.println("param set SYSID_MYGCS 1");
				
				context.registerService(new String[]{APMService.class.getName()}, new APMService(){}, new Hashtable<>());
				
				while(true){
					System.out.println("HERE?! >> "+reader.readLine());
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		});
		t.start();
		context.registerService(new String[]{APMService.class.getName()}, new APMService(){}, new Hashtable<>());

	}

	@Deactivate
	void deactivate() {
		if(apmProcess != null){
			apmProcess.destroy();
		}
	}

}
