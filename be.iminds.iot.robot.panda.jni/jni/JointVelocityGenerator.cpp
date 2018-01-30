#include "JointVelocityGenerator.h"

#include <iostream>

#include <franka/exception.h>
#include <franka/robot.h>

JointVelocityGenerator::JointVelocityGenerator(
		float v1, float v2, float v3, float v4, float v5, float v6, float v7){
	velocity_goal << v1,v2,v3,v4,v5,v6,v7;
	velocity_current << 0,0,0,0,0,0,0;

}

void JointVelocityGenerator::update(
		float v1, float v2, float v3, float v4, float v5, float v6, float v7){
	velocity_goal << v1,v2,v3,v4,v5,v6,v7;
}

franka::JointVelocities JointVelocityGenerator::next(const franka::RobotState& robot_state,
			franka::Duration period){

	for(int i=0;i<7;i++) {
		if(velocity_current(i) < velocity_goal(i) - step) {
			velocity_current(i) = velocity_current(i) + step;
		} else if(velocity_current(i) > velocity_goal(i) + step) {
			velocity_current(i) = velocity_current(i) - step;
		} else {
			velocity_current(i) = velocity_goal(i);
		}
	}

	std::array<double, 7> v;
	Eigen::VectorXd::Map(&v[0], 7) = velocity_current;
	franka::JointVelocities output(v);
	return output;
}
