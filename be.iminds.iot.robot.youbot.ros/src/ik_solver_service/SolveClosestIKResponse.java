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
package ik_solver_service;

public interface SolveClosestIKResponse extends org.ros.internal.message.Message {
  static final java.lang.String _TYPE = "ik_solver_service/SolveClosestIKResponse";
  static final java.lang.String _DEFINITION = "float64[5] \tjoint_angles\nbool \t\tfeasible\nbool \t\tarm_to_front\nbool \t\tarm_bended_up\nbool \t\tgripper_downwards";
  double[] getJointAngles();
  void setJointAngles(double[] value);
  boolean getFeasible();
  void setFeasible(boolean value);
  boolean getArmToFront();
  void setArmToFront(boolean value);
  boolean getArmBendedUp();
  void setArmBendedUp(boolean value);
  boolean getGripperDownwards();
  void setGripperDownwards(boolean value);
}
