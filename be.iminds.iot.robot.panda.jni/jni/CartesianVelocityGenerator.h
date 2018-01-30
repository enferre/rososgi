#pragma once

#include <Eigen/Dense>

#include <franka/control_types.h>
#include <franka/duration.h>
#include <franka/robot_state.h>


class CartesianVelocityGenerator {
public:
	CartesianVelocityGenerator(
			float vx, float vy, float vz, float ox, float oy, float oz);

	void update(float vx, float vy, float vz, float ox, float oy, float oz);

	franka::CartesianVelocities next(const franka::RobotState& robot_state,
			franka::Duration period);

private:
	using Vector6d = Eigen::Matrix<double, 6, 1, Eigen::ColMajor>;

	Vector6d velocity_goal;
	Vector6d velocity_current;

	double step = 0.0025;
};
