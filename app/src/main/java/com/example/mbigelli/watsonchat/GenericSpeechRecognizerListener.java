package com.example.mbigelli.watsonchat;

public interface GenericSpeechRecognizerListener {
    void onRecognitionStart();
    void onRecognitionSuccess(String result);
    void onRecognitionError(String message);
}
