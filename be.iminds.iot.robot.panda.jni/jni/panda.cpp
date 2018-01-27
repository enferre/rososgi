
#include "be_iminds_iot_robot_panda_jni_PandaArmImpl.h"

#include <iostream>

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_init
  (JNIEnv * env, jobject o, jstring s){
	std::cout << "Init!" << std::endl;
}

JNIEXPORT void JNICALL Java_be_iminds_iot_robot_panda_jni_PandaArmImpl_deinit
  (JNIEnv * env, jobject o){
	std::cout << "Done!" << std::endl;
}
