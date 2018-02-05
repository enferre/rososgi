
#include "be_iminds_iot_robot_panda_jni_PandaArmImpl.h"

#include "Java.h"

#include "JointMotionGenerator.h"
#include "JointVelocityGenerator.h"
#include "CartesianMotionGenerator.h"
#include "CartesianVelocityGenerator.h"

#include <iostream>
#include <cmath>
#include <mutex>
#include <atomic>

#include <franka/exception.h>
#include <franka/robot.h>
#include <franka/gripper.h>


Java* java;

franka::Robot* robot;
franka::Gripper* gripper;

float speed = 0.25;

bool moving = false;
bool gripping = false;

int rate = 30;
franka::RobotState robot_state;
franka::GripperState gripper_state;
std::mutex mutex;

CartesianVelocityGenerator cartesian_velocity;
JointVelocityGenerator joint_velocity;

void read_state(){
	if(!moving){
		if (mutex.try_lock()) {
			if(!moving){
				try {
					robot_state = robot->readOnce();
				} catch (franka::Exception const& e) {
					std::cout << "Error reading robot joints: " << e.what() << std::endl;
				}
			}
			mutex.unlock();
		}
	}
}

void start_moving(){
	mutex.lock();
	moving = true;
	mutex.unlock();
}

void stop_moving(){
	mutex.lock();
	moving = false;
	mutex.unlock();
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1init
  (JNIEnv * env, jobject o, jstring s){
	java = new Java(env);

	// init Robot
	const char *ip = env->GetStringUTFChars(s, 0);
	robot = new franka::Robot(ip);
	gripper = new franka::Gripper(ip);
	env->ReleaseStringUTFChars(s, ip);

	robot->setCollisionBehavior(
	        {{20.0, 20.0, 18.0, 18.0, 16.0, 14.0, 12.0}}, {{20.0, 20.0, 18.0, 18.0, 16.0, 14.0, 12.0}},
	        {{20.0, 20.0, 18.0, 18.0, 16.0, 14.0, 12.0}}, {{20.0, 20.0, 18.0, 18.0, 16.0, 14.0, 12.0}},
	        {{20.0, 20.0, 20.0, 25.0, 25.0, 25.0}}, {{20.0, 20.0, 20.0, 25.0, 25.0, 25.0}},
	        {{20.0, 20.0, 20.0, 25.0, 25.0, 25.0}}, {{20.0, 20.0, 20.0, 25.0, 25.0, 25.0}});

	gripper->homing();
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1deinit
  (JNIEnv * env, jobject o){
	delete java;
	delete robot;
	delete gripper;
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1speed
  (JNIEnv * env, jobject o, jfloat s){
	speed = s;
}

JNIEXPORT jfloatArray JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1joints
  (JNIEnv * env, jobject o){
	read_state();

	jfloatArray result = env->NewFloatArray(21);
	if (mutex.try_lock()) {
		int i=0;
		jfloat data[21];
		for(i = 0; i<7 ; i++){
			data[i] = robot_state.q[i];
			data[i+7] = robot_state.dq[i];
			data[i+14] = robot_state.tau_J[i];
		}
		env->SetFloatArrayRegion(result, 0, 21, data);
		mutex.unlock();
	}
	return result;
}


JNIEXPORT jfloatArray JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1pose
  (JNIEnv * env, jobject o){
	read_state();

	jfloatArray result = env->NewFloatArray(12);
	if (mutex.try_lock()) {

		int i=0;
		jfloat data[12];
		// rotation  matrix
		for(int i=0;i<3;i++) {
			for(int j=0;j<3;j++) {
				data[i*3+j] = (float)robot_state.O_T_EE[j*4 + i];
			}
		}
		// position x y z
		data[9] = robot_state.O_T_EE[12];
		data[10] = robot_state.O_T_EE[13];
		data[11] = robot_state.O_T_EE[14];
		env->SetFloatArrayRegion(result, 0, 12, data);

		mutex.unlock();
	}
	return result;
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1stop
  (JNIEnv * env, jobject o){
	try {
	    robot->stop();
	} catch (franka::Exception const& e) {
		std::cout << "Error stopping " << e.what() << std::endl;
		java->throwException(e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1recover
  (JNIEnv * env, jobject o){
	try {
	    robot->automaticErrorRecovery();
	} catch (franka::Exception const& e) {
		std::cout << "Error recovering: " << e.what() << std::endl;
		java->throwException(e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1open
  (JNIEnv * env, jobject o, jobject d, jfloat op){
	try {
		gripping = true;
		gripper->move(op, 0.1);
		gripping = false;
	    java->resolve(d, o);
	} catch (franka::Exception const& e) {
		std::cout << "Error open gripper " << e.what() << std::endl;
		java->fail(d, e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1close
  (JNIEnv * env, jobject o, jobject d, jfloat op, jfloat ef){
	gripping = true;
	try {
		gripper_state = gripper->readOnce();
	} catch (franka::Exception const& e) {
		std::cout << "Error reading gripper state: " << e.what() << std::endl;
	}

	if (gripper_state.is_grasped) {
		gripping = false;
	    java->resolve(d, o);
	    return;
	}

	try {
		gripper->grasp(op, 0.1, ef);
		gripping = false;
	    java->resolve(d, o);
	} catch (franka::Exception const& e) {
		std::cout << e.what() << std::endl;
		java->fail(d, e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1positions
(JNIEnv * env, jobject o, jobject d, jfloat p1, jfloat p2, jfloat p3, jfloat p4, jfloat p5, jfloat p6, jfloat p7) {
	if(moving){
		java->fail(d, "Robot busy with other movement, wait until complete or call stop first!");
	}

	try {
		JointMotionGenerator motion_generator(speed, p1, p2, p3, p4, p5, p6, p7);
		int i=0;
		start_moving();
		robot->control([=, &i, &motion_generator](const franka::RobotState& state,
									franka::Duration time_step) -> franka::JointPositions {
			if(i++ % rate == 0){
				if (mutex.try_lock()) {
					robot_state = state;
				    mutex.unlock();
				}
			}

			return motion_generator.next(state, time_step);
		});
		stop_moving();
		java->resolve(d, o);
	} catch (const franka::Exception& e) {
		stop_moving();
		std::cout << "Error joint position control: " << e.what() << std::endl;
		java->fail(d, e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1velocities
  (JNIEnv * env, jobject o, jfloat v1, jfloat v2, jfloat v3, jfloat v4, jfloat v5, jfloat v6, jfloat v7){
	try {
		if(moving){
			joint_velocity.update(v1, v2, v3, v4, v5, v6, v7);
		} else {
			joint_velocity.goal(v1, v2, v3, v4, v5, v6, v7);
			int i=0;
			start_moving();
			robot->control([=, &i, &joint_velocity](const franka::RobotState& state,
										franka::Duration time_step) -> franka::JointVelocities {
				if(i++ % rate == 0){
					if (mutex.try_lock()) {
						robot_state = state;
						mutex.unlock();
					}
				}

				return joint_velocity.next(state, time_step);
			});
			stop_moving();
		}
	} catch (const franka::Exception& e) {
		stop_moving();
		std::cout << "Error joint velocity control: " << e.what() << std::endl;
		java->throwException(e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1move
  (JNIEnv * env, jobject o, jfloat vx, jfloat vy, jfloat vz, jfloat ox, jfloat oy, jfloat oz){
	try {
		if(moving){
			cartesian_velocity.update(vx, vy, vz, ox, oy, oz);
		} else {
			cartesian_velocity.goal(vx,vy,vz,ox,oy,oz);
			int i=0;
			start_moving();
			robot->control([=, &i, &cartesian_velocity](const franka::RobotState& state,
										franka::Duration time_step) -> franka::CartesianVelocities {
				if(i++ % rate == 0){
					if (mutex.try_lock()) {
						robot_state = state;
						mutex.unlock();
					}
				}
				return cartesian_velocity.next(state, time_step);
			});
			stop_moving();
		}
	} catch (const franka::Exception& e) {
		stop_moving();
		std::cout << "Error cartesian velocity control: " << e.what() << std::endl;
		java->throwException(e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1moveTo
  (JNIEnv * env, jobject o, jobject d, jfloat x, jfloat y, jfloat z, jfloat ox, jfloat oy, jfloat oz, jfloat ow){
	if(moving){
		java->fail(d, "Robot busy with other movement, wait until complete or call stop first!");
	}
	try {
		CartesianMotionGenerator motion_generator(speed, x,y,z, ox,oy,oz,ow);
		int i = 0;
		start_moving();
		robot->control([=, &i, &motion_generator](const franka::RobotState& state,
									franka::Duration time_step) -> franka::CartesianPose {
			if(i++ % rate == 0){
				if (mutex.try_lock()) {
					robot_state = state;
				    mutex.unlock();
				}
			}

			return motion_generator.next(state, time_step);
		});
		stop_moving();
		java->resolve(d, o);
	} catch (const franka::Exception& e) {
		stop_moving();
		std::cout << "Error cartesian position control: " << e.what() << std::endl;
		java->fail(d, e.what());
	}
}


JNIEXPORT jboolean JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl__1is_1grasped
  (JNIEnv * env, jobject o){
	if(!gripping){
		try {
			gripper_state = gripper->readOnce();
		} catch (const franka::Exception& e) {
			std::cout << "Error reading gripper state: " << e.what() << std::endl;
		}
	}

	return gripper_state.is_grasped && gripper_state.width > 0;
}
