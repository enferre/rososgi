#include "CartesianMotionGenerator.h"

#include <iostream>

#include <franka/exception.h>
#include <franka/robot.h>

CartesianMotionGenerator::CartesianMotionGenerator(double speed_factor,
		float x, float y, float z, float ox, float oy, float oz, float ow){
	speed = speed_factor;
	position_goal << x,y,z;
	orientation_goal.vec() << ox, oy, oz;
	orientation_goal.w() =  ow;
}


franka::CartesianPose CartesianMotionGenerator::operator()(
		const franka::RobotState& robot_state, franka::Duration period) {
	time_ += period.toSec();

	if (time_ == 0.0) {
		Eigen::Affine3d transform_start(Eigen::Matrix4d::Map(robot_state.O_T_EE_d.data()));
		position_start = transform_start.translation();
		orientation_start = transform_start.rotation();

		double distance = (position_goal - position_start).norm();
		double adistance = orientation_start.angularDistance(orientation_goal);

		// TODO which are the best v/a max values?
		// TODO also incorporate orientation change here?
		double v_max = 2;
		double a_max = 2.5;

		double T_v = 3.0/(2.0*v_max) * distance;
		double T_a = sqrt(distance*6/a_max);

		T = std::max(T_a,T_v) / speed;
		a2 = 3.0/(T*T);
		a3 = -2.0/(T*T*T);
	}

	double s = a2*time_*time_ + a3*time_*time_*time_;

	Eigen::Vector3d position = position_start + s * (position_goal - position_start);

	//Eigen::Quaterniond orientation = orientation_start.slerp(s, orientation_goal);
	//orientation.normalize();

	// nlerp implementation?
	Eigen::Quaterniond orientation;
	double dot = orientation_start.dot(orientation_goal);
	double si = 1-s;
	if(dot < 0){
		orientation.x() = si*orientation_start.x() - s*orientation_goal.x();
		orientation.y() = si*orientation_start.y() - s*orientation_goal.y();
		orientation.z() = si*orientation_start.z() - s*orientation_goal.z();
		orientation.w() = si*orientation_start.w() - s*orientation_goal.w();

	} else {
		orientation.x() = si*orientation_start.x() + s*orientation_goal.x();
		orientation.y() = si*orientation_start.y() + s*orientation_goal.y();
		orientation.z() = si*orientation_start.z() + s*orientation_goal.z();
		orientation.w() = si*orientation_start.w() + s*orientation_goal.w();
	}
	orientation.normalize();

	// TODO orientation interpolation not working properly?
	//Eigen::Matrix3d mat = orientation.toRotationMatrix();
	Eigen::Matrix3d mat = orientation_start.toRotationMatrix();

	std::array<double, 16> new_pose;
	// TODO is there a better way here?
	new_pose[0] = mat(0,0);
	new_pose[1] = mat(1,0);
	new_pose[2] = mat(2,0);
	new_pose[3] = 0;

	new_pose[4] = mat(0,1);
	new_pose[5] = mat(1,1);
	new_pose[6] = mat(2,1);
	new_pose[7] = 0;

	new_pose[8] = mat(0,2);
	new_pose[9] = mat(1,2);
	new_pose[10] = mat(2,2);
	new_pose[11] = 0;

	new_pose[12] = position(0);
	new_pose[13] = position(1);
	new_pose[14] = position(2);
	new_pose[15] = 1;


//	std::cout << new_pose[0] << " " << new_pose[1] << " " << new_pose[2] << " " << new_pose[3] << std::endl;
//	std::cout << new_pose[4] << " " << new_pose[5] << " " << new_pose[6] << " " << new_pose[7]<< std::endl;
//	std::cout << new_pose[8] << " " << new_pose[9] << " " << new_pose[10] << " " << new_pose[11] << std::endl;
//	std::cout << new_pose[12] << " " << new_pose[13] << " " << new_pose[14] << " " << new_pose[15]<< std::endl << std::endl;


	franka::CartesianPose output(new_pose);
	if (time_ >= T) {
		output.motion_finished = true;
	} else {
		output.motion_finished = false;
	}
	return output;
}
