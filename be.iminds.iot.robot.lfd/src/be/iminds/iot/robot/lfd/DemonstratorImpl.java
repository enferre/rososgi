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
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

import be.iminds.iot.robot.api.JointState;
import be.iminds.iot.robot.api.JointValue;
import be.iminds.iot.robot.api.Orientation;
import be.iminds.iot.robot.api.Pose;
import be.iminds.iot.robot.api.Position;
import be.iminds.iot.robot.api.arm.Arm;
import be.iminds.iot.robot.lfd.api.Demonstration;
import be.iminds.iot.robot.lfd.api.Demonstrator;
import be.iminds.iot.robot.lfd.api.Recording;
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
		  "osgi.command.function=repeat",
		  "osgi.command.function=interrupt",
		  "osgi.command.function=record",
		  "osgi.command.function=stop",
		  "osgi.command.function=mode",
		  "osgi.command.function=guide"},
	immediate=true)
public class DemonstratorImpl implements Demonstrator {

	private String demonstrationsLocation = "demonstrations";
	private String recordingsLocation = "recordings";
	
	// For now limited to one Arm
	private Arm arm;

	// For now only listen for cameras
	private Map<String, Camera> sensors = new ConcurrentHashMap<>();

	private ControllerManager ctrl;
	
	private enum Mode {JOINT, CARTESIAN};
	private Mode mode = Mode.CARTESIAN;
	
	private FloatParser parser = new FloatParser();
	
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
		
		String r = context.getProperty("be.iminds.iot.robot.lfd.recordings.location");
		if(r != null) {
			recordingsLocation = r;
		}
		
		f = new File(recordingsLocation);
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
				writer.write(step.type+"\t");
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
		if(demonstration != null) {
			File demonstrationDir = new File(demonstrationsLocation+File.separator+demonstration);
			if(!demonstrationDir.exists()) {
				// this will create the necessary directories...
				load(demonstration);
			}
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
		if(demonstration != null) {
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
		}
		
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
	
	public Promise<Void> repeat(String demonstration, int times, boolean reversed) {
		Demonstration d = load(demonstration);
		return repeat(d, times, reversed);
	}
	
	@Override
	public Promise<Void> repeat(Demonstration d, int times, boolean reverse){
		final Demonstration toRepeat = new Demonstration();
		for(int i=0;i<d.steps.size();i++) {
			Step step = d.steps.get(i);
			toRepeat.steps.add(step);
			if(step.type == Step.Type.START) {
				// clear everything before step when repeating!
				toRepeat.steps.clear();
			}
		}
		
		return execute(d).then(p -> { 
			if(reverse) {
				return execute(toRepeat, true);
			} else {
				return p;
			}}).then(p -> {
				if(times > 1) {
					return repeat(toRepeat, times-1, reverse);
				} else if(times < 0) {
					// loop infinite if times negative
					return repeat(toRepeat, times, reverse);
				} else {
					return null;
				}
			});
	}
	
	private Promise<Arm> move(Step step){
		if(mode == Mode.JOINT) {
			// move in joint space
			List<JointValue> target = arm.getState().stream()
					.map(js -> js.joint)
					.map(joint -> new JointValue(joint, JointValue.Type.POSITION, parser.parseFloat(step.properties.get(joint))))
					.collect(Collectors.toList());
			
			try {
				return arm.setPositions(target);
			} catch(Throwable t) {
				throw new RuntimeException("Failed executing joint motion", t);
			}
		} else {
			// move in cartesian space
			Position p = null;
			Orientation o = null;
			try {
				float x = parser.parseFloat(step.properties.get("x"));
				float y = parser.parseFloat(step.properties.get("y"));
				float z = parser.parseFloat(step.properties.get("z"));
				
				p = new Position(x,y,z);
				
				if(step.properties.containsKey("o_x")) {
					float ox = parser.parseFloat(step.properties.get("o_x"));
					float oy = parser.parseFloat(step.properties.get("o_y"));
					float oz = parser.parseFloat(step.properties.get("o_z"));
					float ow = parser.parseFloat(step.properties.get("o_w"));
					o = new Orientation(ox, oy, oz, ow);
				} else if(step.properties.containsKey("pitch")) {
					float yaw = parser.parseFloat(step.properties.get("yaw"));
					float pitch = parser.parseFloat(step.properties.get("pitch"));
					float roll = parser.parseFloat(step.properties.get("roll"));
					o = new Orientation(yaw, pitch, roll);
				}
			} catch(Exception e) {
				throw new RuntimeException("This demonstration has no cartesian space information", e);
			}
			
			try {
				if(o == null) {
					return arm.moveTo(p.x, p.y, p.z);
				} else {
					return arm.moveTo(p.x, p.y, p.z, o.x, o.y, o.z, o.w);	
				}
			} catch(Throwable t) {
				throw new RuntimeException("Failed executing cartesian motion", t);
			}
		}
	}

	@Override
	public void interrupt() {
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
	
	private Map<UUID, Recorder> recorders = new ConcurrentHashMap<>();
	
	@Override
	public UUID record(int rate) {
		Recorder r = new Recorder(rate);
		recorders.put(r.id, r);
		return r.start();
	}

	@Override
	public Recording stop(UUID id) {
		Recorder r = recorders.remove(id);
		if(r == null) {
			throw new RuntimeException("Invalid recording id: "+id);
		}
		return r.stop();
	}
	
	@Override
	public List<Recording> stop() {
		// stop all recordings
		List<Recording> result = recorders.values().stream().map(r -> r.stop()).collect(Collectors.toList());
		recorders.clear();
		return result;
	}
	
	private class Recorder {
		
		private long period;
		private UUID id;
		private long start;
		private long end;

		private ScheduledExecutorService recordService = Executors.newSingleThreadScheduledExecutor();
		private PrintWriter writer;
		
		private List<String> header;
		private Map<String, Object> values;
		
		public Recorder(int rate) {
			this.period = (long)(1000000000.0f/rate);
			this.id = UUID.randomUUID();
		}
		
		public UUID start() {
			start = System.currentTimeMillis();
			
			// create the required directories / files
			File recordingLocation = new File(recordingsLocation+File.separator+id);
			if(recordingLocation.exists()) {
				throw new RuntimeException("This recording already exists?! "+recordingLocation.getAbsolutePath());
			}
			
			// create new demonstration and images directory
			File imageDir = new File(recordingsLocation+File.separator+id+File.separator+"images");
			imageDir.mkdirs();
			
			// load steps from csv data
			File csv = new File(recordingsLocation+File.separator+id+File.separator+"recording.csv");
			try {
				writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(csv)));
			} catch (FileNotFoundException e) {
				throw new RuntimeException("Failed to start recording.", e);
			}
			
			// create header
			values = new HashMap<String, Object>();
			List<JointState> joints = arm.getState();
			joints.forEach(j -> {
				values.put("pos_"+j.joint, null);
				values.put("vel_"+j.joint, null);
				values.put("tor_"+j.joint, null);
			});
			try {
				Pose p = arm.getPose();
				values.put("x", null);
				values.put("y", null);
				values.put("z", null);
				values.put("o_x", null);
				values.put("o_y", null);
				values.put("o_z", null);
				values.put("o_w", null);
			} catch(Exception e) {
				// no cartesian pose available
			}
			sensors.entrySet().forEach(e -> {
				values.put(e.getKey(), null);
			});
			
			// TODO other information to record? F_ext, gripper positions, gripper has something, error/collision, ...
			
			header = values.keySet().stream().sorted().collect(Collectors.toList());
			header.forEach(h -> writer.print(h+"\t"));
			writer.println();
			
			// start recording thread
			recordService.scheduleAtFixedRate(()->record(), 0, period, TimeUnit.NANOSECONDS);
			return id;
		}
		
		public Recording stop() {
			// stop recording thread
			recordService.shutdownNow();
			end = System.currentTimeMillis();
			
			writer.flush();
			writer.close();
			
			return new Recording(id, start, end);
		}
		
		public void record() {
			// update values
			List<JointState> joints = arm.getState();
			joints.stream().forEach(j -> {
				values.put("pos_"+j.joint, j.position);
				values.put("vel_"+j.joint, j.velocity);
				values.put("tor_"+j.joint, j.torque);
			});

			if(header.contains("x")) {
				Pose p = arm.getPose();
				values.put("x", p.position.x);
				values.put("y", p.position.y);
				values.put("z", p.position.z);
				values.put("o_x", p.orientation.x);
				values.put("o_y", p.orientation.y);
				values.put("o_z", p.orientation.z);
				values.put("o_w", p.orientation.w);
			}
			
			// get frames for all cameras
			sensors.entrySet().forEach(e -> {
				String fileName = id+File.separator+"images"+File.separator+e.getKey()+"-"+System.currentTimeMillis()+".jpg";
				try {
					toFile(recordingsLocation+File.separator+fileName, (Frame)e.getValue().getValue());
					values.put(e.getKey(), fileName);
				} catch (Exception ex) {
					// ex.printStackTrace();
				}
			});
			
			// write values
			header.stream()
				.map(h -> values.get(h))
				.forEach(value -> writer.print(value+"\t"));
			writer.println();
		}
	}
}

