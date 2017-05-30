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
package be.iminds.iot.robot.api;

public class JointDescription {

	public final String name;
	
	public final float positionMin;
	public final float positionMax;
	
	public final float velocityMin;
	public final float velocityMax;
	
	public final float torqueMin;
	public final float torqueMax;
	
	public JointDescription(String name,
			float pMin, float pMax,
			float vMin, float vMax,
			float tMin, float tMax){
		
		this.name = name;
		
		this.positionMin = pMin;
		this.positionMax = pMax;
		
		this.velocityMin = vMin;
		this.velocityMax = vMax;
		
		this.torqueMin = tMin;
		this.torqueMax = tMax;
	}
	
	public String getName(){
		return name;
	}
	
	public float getPositionMin(){
		return positionMin;
	}
	
	public float getPositionMax(){
		return positionMax;
	}
	
	public float getVelocityMin(){
		return velocityMin;
	}
	
	public float getVelocityMax(){
		return velocityMax;
	}
	
	public float getTorqueMin(){
		return torqueMin;
	}
	
	public float getTorqueMax(){
		return torqueMax;
	}
}
