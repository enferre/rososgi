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
package be.iminds.iot.robot.moveit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import be.iminds.iot.robot.api.arm.Arm;

@Component(service={Object.class},
	property = {"osgi.command.scope=moveit",
	"osgi.command.function=state",
	"osgi.command.function=reset",
	"osgi.command.function=open",
	"osgi.command.function=close",
	"osgi.command.function=position",
	"osgi.command.function=pose",
	"osgi.command.function=halt",
	"osgi.command.function=arm"})
public class MoveItCLI {

	private Arm arm;
	
	public void reset(){
		arm.reset();
	}
	
	public void state(){
		arm.getState().forEach(s -> System.out.println(s.joint+" "+s.position+" "+s.velocity+" "+s.torque));
	}
	
	// positions as index,position pairs
	public void position(String positions){
		String[] pairs = positions.split(" ");
		for(String pair : pairs){
			String[] kv = pair.split(",");
			int joint = Integer.parseInt(kv[0]);
			float val = Float.parseFloat(kv[1]);
			arm.setPosition(joint, val);
		}
	}
	
	public void position(float... positions){
		arm.setPositions(positions);
	}
	
	public void position(int joint, float val){
		arm.setPosition(joint, val);
	}
	
	public void pose(float x, float y, float z){
		arm.moveTo(x,y,z);
	}
	
	public void halt(){
		arm.stop();
	}
	
	public void open(float opening){
		arm.openGripper(opening);
	}
	
	public void open(){
		arm.openGripper();
	}
	
	public void close(){
		arm.closeGripper();
	}
	
	@Reference
	void setArm(Arm arm){
		this.arm = arm;
	}
	
}