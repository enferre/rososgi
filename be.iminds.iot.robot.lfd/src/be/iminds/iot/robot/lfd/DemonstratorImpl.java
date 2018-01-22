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
import java.util.ArrayList;
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
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.lfd.api.Demonstration;
import be.iminds.iot.robot.lfd.api.Demonstrator;
import be.iminds.iot.robot.lfd.api.Step;
import be.iminds.iot.robot.lfd.api.Step.Type;
import be.iminds.iot.sensor.api.Camera;
import be.iminds.iot.sensor.api.Frame;

@Component(
	service= {Demonstrator.class, Object.class},
	property={"osgi.command.scope=lfd",
		  "osgi.command.function=demonstrations",
		  "osgi.command.function=load",
		  "osgi.command.function=step",
		  "osgi.command.function=save",
		  "osgi.command.function=execute",
		  "osgi.command.function=stop",
		  "osgi.command.function=mode",
		  "osgi.command.function=guide"},
	immediate=true)
public class DemonstratorImpl implements Demonstrator {

	private String demonstrationsLocation = "demonstrations";
	
	// For now limited to one Arm
	private Arm arm;

	// For now only listen for cameras
	private Map<String, Camera> sensors = new ConcurrentHashMap<>();

	private ControllerManager ctrl;
	
	private enum Mode {JOINT, CARTESIAN};
	private Mode mode = Mode.CARTESIAN;
	
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
		
		guide(true);
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
	public List<String> demonstrations(){
		List<String> demonstrations = new ArrayList<>();
		File demonstrationsDir = new File(demonstrationsLocation);
		File[] sub = demonstrationsDir.listFiles();
		for(File dir : sub) {
			if(dir.isDirectory()) {
				demonstrations.add(dir.getName());
			}
		}
		return demonstrations;
	}
	
	@Override
	public Demonstration load(String name) {
		Demonstration d = new Demonstration();
		d.name = name;
		
		// check if demonstration exits
		File demoLocation = new File(demonstrationsLocation+File.separator+name);
		if(!demoLocation.exists()) {
			// create new demonstration and images directory
			File imageDir = new File(demonstrationsLocation+File.separator+name+File.separator+"images");
			imageDir.mkdirs();
			return d;
		}
		
		// load steps from csv data
		File csv = new File(demonstrationsLocation+File.separator+name+File.separator+"steps.csv");
		if(csv.exists()) {
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
		}
		
		return d;
	}
	
	@Override
	public void save(Demonstration d) {
		// write steps in csv file
		if(d.steps.size() == 0) {
			// no steps to save
			return;
		}
		
		File csv = new File(demonstrationsLocation+File.separator+d.name+File.separator+"steps.csv");
		try(PrintWriter	writer = new PrintWriter(new FileWriter(csv))){
			// write header
			Step s = d.steps.get(0);
			
			writer.print("type\t");
			List<String> header = s.properties.keySet().stream().sorted().collect(Collectors.toList());
			header.forEach(h -> writer.print(h+"\t"));
			writer.println();
			
			// write steps
			d.steps.forEach(step -> {
				writer.write(s.type+"\t");
				header.stream()
					.map(h -> step.properties.get(h))
					.forEach(value -> writer.print(value+"\t"));
				writer.println();
			});
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Step step(String demonstration, String type) {
		return step(demonstration, Step.Type.valueOf(type));
	}
	
	@Override
	public Step step(String demonstration, Step.Type type) {
		File demonstrationDir = new File(demonstrationsLocation+File.separator+demonstration);
		if(!demonstrationDir.exists()) {
			// this will create the necessary directories...
			load(demonstration);
		}
		
		// record robot state + camera images
		Step step = new Step();
		step.type = type;
		
		// in case of pick/place, also do the gripper action!
		if(step.type == Type.PICK) {
			arm.closeGripper();	
		} else if(step.type == Type.PLACE) {
			arm.openGripper();
		}
		
		arm.getState().stream().forEach(js -> {
			step.properties.put(js.joint, ""+js.position);
		});
		
		try {
			Pose p = arm.getPose();
			step.properties.put("x", ""+p.position.x);
			step.properties.put("y", ""+p.position.y);
			step.properties.put("z", ""+p.position.z);
			
			step.properties.put("o_x", ""+p.orientation.x);
			step.properties.put("o_y", ""+p.orientation.y);
			step.properties.put("o_z", ""+p.orientation.z);
			step.properties.put("o_w", ""+p.orientation.w);
		} catch(Exception e) {
			// in this case cartesian mode is not supported
		}
		
		// get frames for all cameras
		sensors.entrySet().forEach(e -> {
			String fileName = demonstration
								+File.separator+"images"+File.separator+e.getKey()+"-"+System.currentTimeMillis()+".jpg";
			try {
				toFile(demonstrationsLocation+File.separator+fileName, (Frame)e.getValue().getValue());
				step.properties.put(e.getKey(), fileName);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		
		return step;
	}

	public Promise<Void> execute(String demonstration) {
		Demonstration d = load(demonstration);
		return execute(d, false);
	}

	public Promise<Void> execute(String demonstration, boolean reversed) {
		Demonstration d = load(demonstration);
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
		if(step.type == Type.PICK) {
			return move(step)
					.then(p -> reversed ? arm.openGripper() : arm.closeGripper()).then(p -> null);			
		} else if(step.type == Type.PLACE) {
			return move(step)
					.then(p -> reversed ? arm.closeGripper() : arm.openGripper()).then(p -> null);
		} else {
			return move(step).then(p -> null);
		}
	}
	
	private Promise<Arm> move(Step step){
		if(mode == Mode.JOINT) {
			// move in joint space
			List<JointValue> target = arm.getState().stream()
					.map(js -> js.joint)
					.map(joint -> new JointValue(joint, JointValue.Type.POSITION, Float.parseFloat(step.properties.get(joint))))
					.collect(Collectors.toList());
			
			return arm.setPositions(target);
		} else {
			// move in cartesian space
			try {
				float x = Float.parseFloat(step.properties.get("x"));
				float y = Float.parseFloat(step.properties.get("y"));
				float z = Float.parseFloat(step.properties.get("z"));
	
				if(step.properties.containsKey("o_x")) {
					float ox = Float.parseFloat(step.properties.get("o_x"));
					float oy = Float.parseFloat(step.properties.get("o_y"));
					float oz = Float.parseFloat(step.properties.get("o_z"));
					float ow = Float.parseFloat(step.properties.get("o_w"));
					
					return arm.moveTo(x, y, z, ox, oy, oz, ow);
				} else {
					return arm.moveTo(x, y, z);
				}
			} catch(Exception e) {
				throw new RuntimeException("This demonstration has no cartesian space information");
			}
		}
	}

	@Override
	public void stop() {
		arm.stop();
	}

	@Override
	public void recover() {
		arm.recover();
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
	
	public void mode(String m) {
		Mode mode = Mode.valueOf(m);
		this.mode = mode;
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

