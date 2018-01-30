
#include "be_iminds_iot_robot_panda_jni_PandaArmImpl.h"

#include "JointMotionGenerator.h"

#include <iostream>
#include <cmath>

#include <franka/exception.h>
#include <franka/robot.h>
#include <franka/gripper.h>

#include <Eigen/Dense>


JavaVM* jvm;
jclass EXCEPTION_CLASS;
jmethodID EXCEPTION_INIT;
jclass DEFERRED_CLASS;
jmethodID DEFERRED_RESOLVE;
jmethodID DEFERRED_FAIL;

franka::Robot* robot;
franka::Gripper* gripper;

bool moving = false;
float speed = 0.25;
franka::RobotState robot_state;


void throwException(const char * msg){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	env->ThrowNew(EXCEPTION_CLASS, msg );
	jvm->DetachCurrentThread();
}

void resolve(const jobject deferred, const jobject o){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	env->CallVoidMethod(deferred, DEFERRED_RESOLVE, o);
	jvm->DetachCurrentThread();
}

void fail(const jobject deferred, const char* msg){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	jstring message = env->NewStringUTF(msg);
	jobject ex = env->NewObject(EXCEPTION_CLASS, EXCEPTION_INIT, message);
	env->CallVoidMethod(deferred, DEFERRED_FAIL, ex);
	jvm->DetachCurrentThread();
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_init
  (JNIEnv * env, jobject o, jstring s){
	// keep ref to the Java VM
	env->GetJavaVM(&jvm);

	// Java exception handling
	jclass exceptionClass;
	char *exClassName = (char*)"java/lang/Exception";
	exceptionClass = env->FindClass(exClassName);
    EXCEPTION_CLASS = (jclass) env->NewGlobalRef(exceptionClass);
    EXCEPTION_INIT = env->GetMethodID(EXCEPTION_CLASS, "<init>", "(Ljava/lang/String;)V");

	// Resolve promises
	jclass deferredClass;
	char *deferredClassName = (char*)"org/osgi/util/promise/Deferred";
	deferredClass = env->FindClass(deferredClassName);
	DEFERRED_CLASS = (jclass) env->NewGlobalRef(deferredClass);
	DEFERRED_RESOLVE = env->GetMethodID(DEFERRED_CLASS, "resolve", "(Ljava/lang/Object;)V");
	DEFERRED_FAIL = env->GetMethodID(DEFERRED_CLASS, "fail", "(Ljava/lang/Throwable;)V");

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

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_deinit
  (JNIEnv * env, jobject o){
	std::cout << "Done!" << std::endl;

	env->DeleteGlobalRef(DEFERRED_CLASS);
	env->DeleteGlobalRef(EXCEPTION_CLASS);

	delete robot;
	delete gripper;
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_speed
  (JNIEnv * env, jobject o, jfloat s){
	speed = s;
}

JNIEXPORT jfloatArray JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_joints
  (JNIEnv * env, jobject o){
	if(!moving){
		robot_state = robot->readOnce();
	}

	jfloatArray result = env->NewFloatArray(21);
	int i=0;
	jfloat data[21];
	for(i = 0; i<7 ; i++){
		data[i] = robot_state.q[i];
		data[i+7] = robot_state.dq[i];
		data[i+14] = robot_state.tau_J[i];
	}
	env->SetFloatArrayRegion(result, 0, 21, data);
	return result;
}


JNIEXPORT jfloatArray JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_pose
  (JNIEnv * env, jobject o){
	if(!moving){
		robot_state = robot->readOnce();
	}

	jfloatArray result = env->NewFloatArray(12);
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
	return result;
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_stop
  (JNIEnv * env, jobject o, jobject d){
	try {
	    robot->stop();
	    resolve(d, o);
	} catch (franka::Exception const& e) {
		fail(d, e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_recover
  (JNIEnv * env, jobject o, jobject d){
	try {
	    robot->automaticErrorRecovery();
	    resolve(d, o);
	} catch (franka::Exception const& e) {
		fail(d, e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_open
  (JNIEnv * env, jobject o, jobject d, jfloat op){
	try {
		gripper->move(op, 0.1);
	    resolve(d, o);
	} catch (franka::Exception const& e) {
		fail(d, e.what());
	}
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_close
  (JNIEnv * env, jobject o, jobject d, jfloat op, jfloat ef){
	try {
		gripper->grasp(op, 0.1, ef);
	    resolve(d, o);
	} catch (franka::Exception const& e) {
		fail(d, e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_positions
(JNIEnv * env, jobject o, jobject d, jfloat p1, jfloat p2, jfloat p3, jfloat p4, jfloat p5, jfloat p6, jfloat p7) {
	try {
		std::array<double, 7> q_goal;
		q_goal[0] = p1;
		q_goal[1] = p2;
		q_goal[2] = p3;
		q_goal[3] = p4;
		q_goal[4] = p5;
		q_goal[5] = p6;
		q_goal[6] = p7;

		JointMotionGenerator motion_generator(speed, q_goal);
		robot->control(motion_generator);
		resolve(d, o);
	} catch (const franka::Exception& e) {
		fail(d, e.what());
	}
}


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_moveTo
  (JNIEnv * env, jobject o, jobject d, jfloat x, jfloat y, jfloat z, jfloat ox, jfloat oy, jfloat oz, jfloat ow){
	try {
		auto initial_pose = robot->readOnce().O_T_EE_d;

		Eigen::Affine3d initial_transform(Eigen::Matrix4d::Map(initial_pose.data()));
		Eigen::Vector3d initial_position(initial_transform.translation());
		Eigen::Quaterniond initial_orientation(initial_transform.rotation());

		Eigen::Vector3d desired_position(x,y,z);
		Eigen::Quaterniond desired_orientation(ow, ox, oy, oz);

		double distance = (desired_position - initial_position).norm();
		double adistance = initial_orientation.angularDistance(desired_orientation);

		// TODO which are the best v/a max values?
		// TODO also incorporate orientation change here?
		double v_max = 2;
		double a_max = 2.5;

		double T_v = 3.0/(2.0*v_max) * distance;
		double T_a = sqrt(distance*6/a_max);
		double T = std::max(T_a,T_v) / speed;

		double a2 = 3.0/(T*T);
		double a3 = -2.0/(T*T*T);

		double time = 0.0;
		robot->control(
				[=, &time](const franka::RobotState&, franka::Duration time_step) -> franka::CartesianPose {
					time += time_step.toSec();
					double s = a2*time*time + a3*time*time*time;

					Eigen::Vector3d position = initial_position + s * (desired_position - initial_position);

					//Eigen::Quaterniond orientation = initial_orientation.slerp(s, desired_orientation);
					//orientation.normalize();

					// nlerp implementation?
					Eigen::Quaterniond orientation;
					double dot = initial_orientation.dot(desired_orientation);
					double si = 1-s;
					if(dot < 0){
						orientation.x() = si*initial_orientation.x() - s*desired_orientation.x();
						orientation.y() = si*initial_orientation.y() - s*desired_orientation.y();
						orientation.z() = si*initial_orientation.z() - s*desired_orientation.z();
						orientation.w() = si*initial_orientation.w() - s*desired_orientation.w();

					} else {
						orientation.x() = si*initial_orientation.x() + s*desired_orientation.x();
						orientation.y() = si*initial_orientation.y() + s*desired_orientation.y();
						orientation.z() = si*initial_orientation.z() + s*desired_orientation.z();
						orientation.w() = si*initial_orientation.w() + s*desired_orientation.w();
					}
					orientation.normalize();

					// TODO orientation interpolation not working properly?
					//Eigen::Matrix3d mat = orientation.toRotationMatrix();
					Eigen::Matrix3d mat = initial_orientation.toRotationMatrix();

					std::array<double, 16> new_pose = initial_pose;

					// TODO is there a better way here?
					new_pose[0] = mat(0,0);
					new_pose[1] = mat(1,0);
					new_pose[2] = mat(2,0);

					new_pose[4] = mat(0,1);
					new_pose[5] = mat(1,1);
					new_pose[6] = mat(2,1);

					new_pose[8] = mat(0,2);
					new_pose[9] = mat(1,2);
					new_pose[10] = mat(2,2);

					new_pose[12] = position[0];
					new_pose[13] = position[1];
					new_pose[14] = position[2];

					//std::cout << new_pose[0] << " " << new_pose[1] << " " << new_pose[2] << std::endl;
					//std::cout << new_pose[4] << " " << new_pose[5] << " " << new_pose[6] << std::endl;
					//std::cout << new_pose[8] << " " << new_pose[9] << " " << new_pose[10] << std::endl << std::endl;

					if (time >= T) {
						resolve(d, o);
						return franka::MotionFinished(new_pose);
					}
					return new_pose;
				});
	} catch (const franka::ControlException& e) {
		fail(d, e.what());
	}
}
