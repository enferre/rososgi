#pragma once

#include "Java.h"

Java::Java(JNIEnv * env){
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
}

Java::~Java(){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	env->DeleteGlobalRef(DEFERRED_CLASS);
	env->DeleteGlobalRef(EXCEPTION_CLASS);
	jvm->DetachCurrentThread();
}

void Java::throwException(const char * msg){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	env->ThrowNew(EXCEPTION_CLASS, msg );
	jvm->DetachCurrentThread();
}


void Java::resolve(const jobject deferred, const jobject o){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	env->CallVoidMethod(deferred, DEFERRED_RESOLVE, o);
	jvm->DetachCurrentThread();
}


void Java::fail(const jobject deferred, const char* msg){
	JNIEnv* env;
	jvm->AttachCurrentThread((void**)&env, NULL);
	jstring message = env->NewStringUTF(msg);
	jobject ex = env->NewObject(EXCEPTION_CLASS, EXCEPTION_INIT, message);
	env->CallVoidMethod(deferred, DEFERRED_FAIL, ex);
	jvm->DetachCurrentThread();
}


