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
package be.iminds.iot.input.joystick.api;

import java.util.Arrays;

public class JoystickEvent {

	public enum JoystickButton {
		BUTTON_X,
		BUTTON_A,
		BUTTON_B,
		BUTTON_Y,
		BUTTON_L1,
		BUTTON_R1,
		BUTTON_L2,
		BUTTON_R2,
		BUTTON_SELECT,
		BUTTON_START
	}
	
	public enum Type {
		BUTTON_X_PRESSED,
		BUTTON_A_PRESSED,
		BUTTON_B_PRESSED,
		BUTTON_Y_PRESSED,
		BUTTON_L1_PRESSED,
		BUTTON_R1_PRESSED,
		BUTTON_L2_PRESSED,
		BUTTON_R2_PRESSED,
		BUTTON_SELECT_PRESSED,
		BUTTON_START_PRESSED,
		BUTTON_X_RELEASED,
		BUTTON_A_RELEASED,
		BUTTON_B_RELEASED,
		BUTTON_Y_RELEASED,
		BUTTON_L1_RELEASED,
		BUTTON_R1_RELEASED,
		BUTTON_L2_RELEASED,
		BUTTON_R2_RELEASED,
		BUTTON_SELECT_RELEASED,
		BUTTON_START_RELEASED,
		JOYSTICK_CHANGED,

	}
	
	public final Type type;
	public final float[] axes;
	public final int[] buttons;
	
	public JoystickEvent(Type type, float[] axes, int[] buttons){
		this.type = type;
		this.axes = axes;
		this.buttons = buttons;
	}
	
	public boolean isPressed(JoystickButton button){
		return buttons[button.ordinal()] == 1;
	}
	
	@Override
	public String toString(){
		return type+" "+Arrays.toString(axes)+" "+Arrays.toString(buttons);
	}
}
