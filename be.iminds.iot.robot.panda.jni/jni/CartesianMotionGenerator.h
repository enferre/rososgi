// Copyright (c) 2017 Franka Emika GmbH
// Use of this source code is governed by the Apache-2.0 license, see LICENSE
#pragma once

#include <Eigen/Dense>

#include <franka/control_types.h>
#include <franka/duration.h>
#include <franka/robot_state.h>

/**
 * An example showing how to generate a cartesian motion to a goal position and orientation.
 */
class CartesianMotionGenerator {
public:
	/**
	 * Creates a new CartesianMotionGenerator instance for a target pose / orientation.
	 *
	 * @param[in] speed_factor General speed factor in range [0, 1].
	 * @param[in] q_goal Target joint positions.
	 */
	CartesianMotionGenerator(double speed_factor,
			float x, float y, float z, float ox, float oy, float oz, float ow);

	/**
	 * Sends cartesian pose calculations
	 *
	 * @param[in] robot_state Current state of the robot.
	 * @param[in] period Duration of execution.
	 *
	 * @return Cartesian pose for use inside a control loop.
	 */
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
