package com.example.mbigelli.watsonchat;

// Unlike the iOS version, this will handle both file download and media playback. This sucks.
public interface SynthesisListener {
    void onSynthesisStart();
    void onSynthesisSuccess();
    void onSpeechStart();
    void onSpeechSuccess();
    void onSynthesisError(String message); // No point splitting errors for now.
}
