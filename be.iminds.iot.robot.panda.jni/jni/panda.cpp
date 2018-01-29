
#include "be_iminds_iot_robot_panda_jni_PandaArmImpl.h"

#include <iostream>

#include <franka/exception.h>
#include <franka/robot.h>
#include <franka/gripper.h>


JavaVM* jvm;
jclass EXCEPTION_CLASS;
jmethodID EXCEPTION_INIT;
jclass DEFERRED_CLASS;
jmethodID DEFERRED_RESOLVE;
jmethodID DEFERRED_FAIL;

franka::Robot* robot;
franka::Gripper* gripper;
float speed = 0.25;


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

}


JNIEXPORT jfloatArray JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_pose
  (JNIEnv * env, jobject o){

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


JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_moveTo
  (JNIEnv * env, jobject o, jobject d, jfloat x, jfloat y, jfloat z, jfloat ox, jfloat oy, jfloat oz, jfloat ow){
	std::cout << "Move to " << x << " " << y << " " << z << std::endl;

	resolve(d, o);
	//fail(d, "Something went wrong!");
}
