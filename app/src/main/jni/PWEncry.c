#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

JNIEXPORT jstring JNICALL Java_com_arbo_myjni_MainActivity_myHelloFromJni(JNIEnv* env, jobject this){
    char* cstr = "hahaha ,this is C code";
    return ((*env)->NewStringUTF(env, cstr));
}

jint Java_com_arbo_myjni_MainActivity_myadd(JNIEnv* env, jobject obj, jint a, jint b){
    return a+b;
}

char*   Jstring2CStr(JNIEnv*   env, jstring   jstr){
    char* cstr = NULL;
    jclass classStr = (*env)->FindClass(env, "java/lang/String");
    jstring strencode = (*env)->NewStringUTF(env, "GB2312");
    jmethodID mid = (*env)->GetMethodID(env, classStr, "getBytes", "(Ljava/lang/String;)[B");
    jbyteArray barr = (*env)->CallObjectMethod(env, jstr, mid, strencode);

    jint barrlength = (*env)->GetArrayLength(env, barr);
    jbyte* arrp = (*env)->GetByteArrayElements(env, barr, JNI_FALSE);
    if (barrlength>0){
        cstr = (char*)malloc(sizeof(char)*barrlength+1);

        memcpy(cstr, arrp, barrlength);
        cstr[barrlength] = 0;
    }

    (*env)->ReleaseByteArrayElements(env, barr, arrp, 0);
    return cstr;
}

JNIEXPORT jstring JNICALL Java_com_arbo_hero_util_MsgDigest_encryFromJni
(JNIEnv * env, jobject obj, jstring input, jint length){
    char *cstr = Jstring2CStr(env,input);
    int i;
    for (i=0;i<length;i++){
        *(cstr+i) +=2;
    }
    return (*env)->NewStringUTF(env, cstr);
}

JNIEXPORT jstring JNICALL Java_com_arbo_hero_util_MsgDigest_decryFromJni
(JNIEnv * env, jobject obj, jstring input, jint length){
    char *cstr = Jstring2CStr(env,input);
    int i;
    for (i=0;i<length;i++){
        *(cstr+i) -=2;
    }
    return (*env)->NewStringUTF(env, cstr);
}