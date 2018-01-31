#include "CartesianVelocityGenerator.h"

#include <iostream>

#include <franka/exception.h>
#include <franka/robot.h>


CartesianVelocityGenerator::CartesianVelocityGenerator(){
	velocity_goal << 0,0,0,0,0,0;
	velocity_current << 0,0,0,0,0,0;
}

void CartesianVelocityGenerator::goal(
			float vx, float vy, float vz, float ox, float oy, float oz){
	velocity_goal << vx,vy,vz,ox,oy,oz;
	velocity_current << 0,0,0,0,0,0;
}


void CartesianVelocityGenerator::update(float vx, float vy, float vz, float ox, float oy, float oz){
	velocity_goal << vx,vy,vz,ox,oy,oz;
}


franka::CartesianVelocities CartesianVelocityGenerator::next(const franka::RobotState& robot_state,
			franka::Duration period){

	for(int i=0;i<6;i++) {
		if(velocity_current(i) < velocity_goal(i) - step) {
			velocity_current(i) = velocity_current(i) + step;
		} else if(velocity_current(i) > velocity_goal(i) + step) {
			velocity_current(i) = velocity_current(i) - step;
		} else {
			velocity_current(i) = velocity_goal(i);
		}
	}

	std::array<double, 6> v;
	Eigen::VectorXd::Map(&v[0], 6) = velocity_current;
	franka::CartesianVelocities output(v);

	// finish if all velocities are 0
	output.motion_finished = true;
	for(int i=0;i<6;i++) {
		if(velocity_current(i) != 0){
			output.motion_finished = false;
			break;
		}
	}

	return output;
}
