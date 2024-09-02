#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_accesstrigger_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string url = "https://www.example.com";
    return env->NewStringUTF(url.c_str());
}
extern "C" JNIEXPORT jboolean JNICALL
Java_com_example_accesstrigger_MainActivity_verifyCredentials(
        JNIEnv *env,
        jobject,
        jstring username, jstring password) {

    // TODO: implement verifyCredentials()
}