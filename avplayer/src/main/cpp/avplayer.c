//
// Created by beiying on 2019/5/3.
//

#include <jni.h>
#include <string.h>
#include <sys/ptrace.h>

#include <libavcodec/avcodec.h>
#include <libswscale/swscale.h>
#include <libavformat/avformat.h>
#include <libavutil/avutil.h>
#include <libavutil/frame.h>
#include <libavdevice/avdevice.h>
#include <libavfilter/avfilter.h>

#include "libswresample/swresample.h"
#include "libavutil/opt.h"
#include "libavutil/imgutils.h"

// 获取数组的大小
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))

// 指定要注册的类，对应完整的java类名
#define JNIREG_CLASS "com/beiying/avplayer/BYAvPlayerView"


JNIEXPORT jstring JNICALL urlProtocolInfo(JNIEnv *env) {
}


JNIEXPORT jstring JNICALL avFormatInfo(JNIEnv *env) {

}

JNIEXPORT jstring JNICALL avCodecInfo(JNIEnv *env) {

}

JNIEXPORT jstring JNICALL avFilterInfo(JNIEnv *env) {

}

void JNICALL playVideo(JNIEnv *env, jstring videoPath, jobject surface) {

}

static int registerNativeMethods(JNIEnv *env, const char *className, JNINativeMethod *gMethods, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

static JNINativeMethod jni_Methods_table[] = {
        {"urlProtocolInfo", "(V;)Ljava/lang/String;", (void *) urlProtocolInfo},
        {"avFormatInfo", "(V;)Ljava/lang/String;", (void *) avFormatInfo},
        {"avCodecInfo", "(V;)Ljava/lang/String;", (void *) avCodecInfo},
        {"avFilterInfo", "(V;)Ljava/lang/String;", (void *) avFilterInfo},
        {"playVideo", "(Ljava/lang/String;Ljava/lang/Object;)V;", (void *) playVideo}
};

int register_ndk_onload(JNIEnv *env) {
    return registerNativeMethods(env, JNIREG_CLASS, jni_Methods_table, NELEM(jni_Methods_table));
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    //这是一种比较简单的防止被调试的方案
    // 有更复杂更高明的方案，比如：不用这个ptrace而是每次执行加密解密签先去判断是否被trace
    ptrace(PTRACE_TRACEME, 0, 0, 0);

    JNIEnv *env = NULL;
    jint result = -1;
    if ((*vm)->GetEnv(vm, (void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return result;
    }
    register_ndk_onload(env);
    // 返回jni的版本
    return JNI_VERSION_1_4;
}