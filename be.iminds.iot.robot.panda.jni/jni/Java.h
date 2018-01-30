#pragma once

#include <jni.h>

class Java {
public:
	/**
	 * Create Java interface
	 */
	Java(JNIEnv * env);

	/**
	 * Destructor
	 */
	~Java();

	/**
	 * Throw an exception to Java
	 */
	void throwException(const char * msg);

	/**
	 * Resolve a deferred
	 */
	void resolve(const jobject deferred, const jobject o);

	/**
	 * Fail a deferred
	 */
	void fail(const jobject deferred, const char* msg);

private:
	JavaVM* jvm;
	jclass EXCEPTION_CLASS;
	jmethodID EXCEPTION_INIT;
	jclass DEFERRED_CLASS;
	jmethodID DEFERRED_RESOLVE;
	jmethodID DEFERRED_FAIL;
};
