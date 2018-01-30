#pragma once

#include <Eigen/Dense>

#include <franka/control_types.h>
#include <franka/duration.h>
#include <franka/robot_state.h>


class JointVelocityGenerator {
public:
	JointVelocityGenerator(float v1, float v2, float v3, float v4, float v5, float v6, float v7);

	void update(float v1, float v2, float v3, float v4, float v5, float v6, float v7);

	franka::JointVelocities next(const franka::RobotState& robot_state,
			franka::Duration period);

private:
	using Vector7d = Eigen::Matrix<double, 7, 1, Eigen::ColMajor>;

	Vector7d velocity_goal;
	Vector7d velocity_current;

	double step = 0.0025;
};
