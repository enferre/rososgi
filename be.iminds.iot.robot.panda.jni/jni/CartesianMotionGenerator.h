#pragma once

#include <Eigen/Dense>

#include <franka/control_types.h>
#include <franka/duration.h>
#include <franka/robot_state.h>


class CartesianMotionGenerator {
public:
	CartesianMotionGenerator(double speed_factor,
			float x, float y, float z, float ox, float oy, float oz, float ow);

	franka::CartesianPose operator()(const franka::RobotState& robot_state,
			franka::Duration period);

private:
	double time_ = 0.0;

	Eigen::Vector3d position_start;
	Eigen::Quaterniond orientation_start;

	Eigen::Vector3d position_goal;
	Eigen::Quaterniond orientation_goal;

	double speed;
	double T;
	double a2;
	double a3;
};
