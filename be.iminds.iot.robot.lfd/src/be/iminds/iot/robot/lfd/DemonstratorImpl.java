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
package be.iminds.iot.robot.lfd;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.lfd.api.Demonstration;
import be.iminds.iot.robot.lfd.api.Demonstrator;
import be.iminds.iot.robot.lfd.api.Step;
import be.iminds.iot.robot.lfd.api.Step.Type;
import be.iminds.iot.sensor.api.Camera;
import be.iminds.iot.sensor.api.Frame;

@Component(
	property={"osgi.command.scope=lfd",
		  "osgi.command.function=start",
		  "osgi.command.function=step",
		  "osgi.command.function=finish",
		  "osgi.command.function=cancel",
		  "osgi.command.function=execute",
		  "osgi.command.function=stop",
		  "osgi.command.function=guide"},
	immediate=true)
public class DemonstratorImpl implements Demonstrator {

	private String demonstrationsLocation = "demonstrations";
	
	// For now limited to single recording
	private Demonstration current;
	
	private PrintWriter writer;
	
	// For now limited to one Arm
	private Arm arm;
	private float opening = 0.08f;

	// For now only listen for cameras
	private Map<String, Camera> sensors = new ConcurrentHashMap<>();

	private ControllerManager ctrl;
	
	@Activate
	void activate(BundleContext context) {
		String l = context.getProperty("be.iminds.iot.robot.lfd.demonstrations.location");
		if(l != null) {
			demonstrationsLocation = l;
		}
		
		File f = new File(demonstrationsLocation);
		if(!f.exists() || !f.isDirectory()) {
			f.mkdirs();
		}
	}
	
	@Reference
	void setControllerManager(ControllerManager c) {
		this.ctrl = c;
	}
	
	@Reference
	void setArm(Arm a) {
		this.arm = a;
	}
	
	@Reference(cardinality=ReferenceCardinality.MULTIPLE, 
			policy=ReferencePolicy.DYNAMIC)
	void addCamera(Camera c, Map<String, String> properties) {
		String name = properties.get("name");
		sensors.put(name, c);
	}
	
	void removeCamera(Camera c, Map<String, String> properties) {
		sensors.values().remove(c);
	}
	
	@Override
	public void start(String name) {
		current = new Demonstration();
		current.name = name;

		File demoLocation = new File(demonstrationsLocation+File.separator+current.name+File.separator+"images");
		if(demoLocation.exists()) {
			throw new RuntimeException("Invalid location, already exists: "+demoLocation.getAbsolutePath());
		}
		demoLocation.mkdirs();
		
		File csv = new File(demonstrationsLocation+File.separator+current.name+File.separator+"steps.csv");
		try {
			
			writer = new PrintWriter(new FileWriter(csv));
			// write header
			writer.print("type\t");

			arm.getState().stream().forEach(js -> {
				writer.print(js.joint+"\t");
			});
			writer.print("gripper\t");
			
			sensors.entrySet().forEach(e -> {
				writer.print(e.getKey()+"\t");
			});
			writer.println();
		} catch (IOException e) {
			e.printStackTrace();
			cancel();
		}
	}

	@Override
	public Step step(String type) {
		if(current == null) {
			throw new RuntimeException("No demonstration is being captured, call start first");
		}
		
		// record robot state + camera images
		Step step = new Step();
		step.type = Type.valueOf(type);
		
		// in case of pick/place, also do the gripper action!
		if(step.type == Type.PICK) {
			opening = 0.0f;
			arm.openGripper(opening);			
		} else if(step.type == Type.PLACE) {
			opening = 0.08f;
			arm.openGripper(opening);
		}
		
		writer.print(type+"\t");
		
		arm.getState().stream().forEach(js -> {
				step.properties.put(js.joint, ""+js.position);
				writer.print(js.position+"\t");
			});
		// TODO get the gripper opening?!
		step.properties.put("gripper", ""+opening);
		writer.print(opening+"\t");

		// get frames for all cameras
		sensors.entrySet().forEach(e -> {
			String fileName = demonstrationsLocation+File.separator+current.name
								+File.separator+"images"+File.separator+e.getKey()+"-"+current.steps.size()+".jpg";
			try {
				toFile(fileName, (Frame)e.getValue().getValue());
				writer.print(fileName+"\t");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		writer.println();
		writer.flush();

		current.steps.add(step);
		
		return step;
	}

	@Override
	public Demonstration finish() {
		if(current == null) {
			throw new RuntimeException("No demonstration is being captured, call start first");
		}

		writer.flush();
		writer.close();
		writer = null;
		
		Demonstration d = current;
		current = null;
		
		return d;
	}

	@Override
	public void cancel() {
		if(current != null) {
			File demoLocation = new File(demonstrationsLocation+File.separator+current.name);
			demoLocation.delete();
			current = null;
		}
	}	
	
	@Override
	public Promise<Void> execute(String demonstration, boolean reversed) {
		Demonstration d = new Demonstration();
		d.name = demonstration;
		
		// read step csv file
		File csv = new File(demonstrationsLocation+File.separator+demonstration+File.separator+"steps.csv");
		try (BufferedReader reader = new BufferedReader(new FileReader(csv))){
			String header = reader.readLine();
			String[] keys = header.split("\t");
			
			String line = reader.readLine();
			while(line != null) {
				String[] values = line.split("\t");
				Step step = new Step();
				for(int i=0;i<keys.length;i++) {
					if(keys[i].equals("type")) {
						step.type = Type.valueOf(values[i]);
					} else {
						step.properties.put(keys[i], values[i]);
					}
				}
				d.steps.add(step);
				line = reader.readLine();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return execute(d, reversed);
	}
	
	@Override
	public Promise<Void> execute(Demonstration d, boolean reversed) {
		if(reversed) {
			return execute(d, d.steps.size()-1, reversed);
		} else {
			return execute(d, 0, reversed);
		}
	}
	
	private Promise<Void> execute(Demonstration d, int step, boolean reversed){
		Deferred<Void> deferred = new Deferred<>();
		if(reversed && step < 0) {
			deferred.resolve(null);
		} else if(step == d.steps.size()) {
			deferred.resolve(null);
		} else {
			Step toExecute = d.steps.get(step);
			deferred.resolveWith(execute(toExecute, reversed).then(p -> reversed ? execute(d, step-1, reversed) : execute(d, step+1, reversed)));
		}
			
		return deferred.getPromise();
	}

	@Override
	public Promise<Void> execute(Step step, boolean reversed) {
		List<JointValue> target = arm.getState().stream()
				.map(js -> js.joint)
				.map(joint -> new JointValue(joint, JointValue.Type.POSITION, Float.parseFloat(step.properties.get(joint))))
				.collect(Collectors.toList());

		if(step.type == Type.PICK) {
			float gripperOpening = reversed ? 0.08f : 0.0f;
			return arm.setPositions(target).then(p -> arm.openGripper(gripperOpening)).then(p -> null);			
		} else if(step.type == Type.PLACE) {
			float gripperOpening = reversed ? 0.0f : 0.08f;
			return arm.setPositions(target).then(p -> arm.openGripper(gripperOpening)).then(p -> null);
		} else {
			return arm.setPositions(target).then(p -> null);
		}
	}

	@Override
	public void stop() {
		arm.stop();
	}

	
	private void toFile(String fileName, Frame f) throws Exception {
		// TODO faster writing to file required?
		BufferedImage img = new BufferedImage(f.width, f.height,
				BufferedImage.TYPE_INT_RGB);
		
		int c1 = 0;
		int c2 = f.width*f.height;
		int c3 = 2*f.width*f.height;
		
		int r=0,g=0,b=0,a=0,col;
		
		for (int j = 0; j < f.height; j++) {
			for (int i = 0; i < f.width; i++) {
				
				r = (int)(f.data[c1++]*255f);
				g = (int)(f.data[c2++]*255f);
				b = (int)(f.data[c3++]*255f);
				a = 255;

				col = a << 24 | r << 16 | g << 8 | b;
				img.setRGB(i, j, col);
			}
		}
		
		String formatName = fileName.substring(fileName.length()-3);
		ImageIO.write(img, formatName, new File(fileName));
	}

	public void guide() {
		guide(true);
	}
	
	@Override
	public void guide(boolean guide) {
		// TODO configure controller?!
		if(guide) {
			ctrl.stop("effort_joint_trajectory_controller");
		} else {
			ctrl.start("effort_joint_trajectory_controller");
		}
	}

}

