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

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Parses floats from demonstration expressions.
 * 
 * Allows for basic variable assignments, random ranges and sine/cosine/negation functions.
 */
public class FloatParser {

	// TODO use a new hashmap per demonstration?
	private Map<String, Float> assignments = new HashMap<>();
	// TODO use seed for repeatable experiments?
	private Random random = new Random(System.currentTimeMillis());
	
	public float parseFloat(String s) {
		if(s.startsWith("[")) {
			// generate uniform random number in range
			String[] split = s.substring(1, s.length()-1).split(",");
			float min = Float.parseFloat(split[0]);
			float max = Float.parseFloat(split[1]);
			float r = min+random.nextFloat()*(max-min);
			return r;
		} else if(s.startsWith("sin(")) {
			String arg = s.substring(4, s.length()-1);
			return (float) Math.sin(parseFloat(arg));
		} else if(s.startsWith("cos(")) {
			String arg = s.substring(4, s.length()-1);
			return (float) Math.cos(parseFloat(arg));
		} else if(s.startsWith("-")) {
			return - parseFloat(s.substring(1));
		} else if(s.contains("=")) {
			// assign a key to a value for future reuse in the demonstration
			// TODO check if only 
			String key = s.substring(0,s.indexOf("=")).trim();
			float value = parseFloat(s.substring(s.indexOf("=")+1).trim());
			assignments.put(key, value);
			return value;
		}
		
		// no assignment or range, try to convert to float
		try {
			return Float.parseFloat(s);
		} catch(NumberFormatException e) {
			// this  is not a number, try if it is an assigned key
			if(assignments.containsKey(s)) {
				return assignments.get(s);
			} else {
				throw new RuntimeException("Invalid value: "+s);
			}
		}
	}
}
