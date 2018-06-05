package com.example.mbigelli.watsonchat;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.speech.SpeechRecognizer.RESULTS_RECOGNITION;

public class NativeSpeechRecognizer implements GenericSpeechRecognizer, RecognitionListener {

    private Context context;
    private SpeechRecognizer speechRecognizer;
    private WeakReference<GenericSpeechRecognizerListener> listenerWeakReference;

    public NativeSpeechRecognizer(GenericSpeechRecognizerListener listener, Context context) {
        this.context = context;
        listenerWeakReference = new WeakReference(listener);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(this);
    }

    private void setMute(Boolean mute) {
        AudioManager amanager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            amanager.adjustStreamVolume(AudioManager.STREAM_MUSIC, mute ? AudioManager.ADJUST_MUTE : AudioManager.ADJUST_UNMUTE, 0);
        } else {
            amanager.setStreamMute(AudioManager.STREAM_SYSTEM, mute);
        }
    }

    // GenericSpeechRecognizer methods
    @Override
    public void start() {
        listenerWeakReference.get().onRecognitionStart();
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");
        speechRecognizer.startListening(intent);
    }

    @Override
    public void cancel() {
        if (speechRecognizer != null) {
//            speechRecognizer.cancel();
//            speechRecognizer.destroy();
        }
    }

    // RecognitionListener methods
    @Override
    public void onReadyForSpeech(Bundle params) {
    }

    @Override
    public void onResults(Bundle results) {
        ArrayList<String> resultTexts = results.getStringArrayList(RESULTS_RECOGNITION);
        String bestTranscription = resultTexts.get(0);
        listenerWeakReference.get().onRecognitionSuccess(bestTranscription);
//        speechRecognizer.destroy();
    }

    @Override
    public void onError(int error) {
        listenerWeakReference.get().onRecognitionError(String.valueOf(error));
//        speechRecognizer.destroy();
    }

    @Override
    public void onEndOfSpeech() {

    }
    @Override
    public void onBeginningOfSpeech() {}
    @Override
    public void onRmsChanged(float rmsdB) {}
    @Override
    public void onBufferReceived(byte[] buffer) {}
    @Override
    public void onPartialResults(Bundle partialResults) {}
    @Override
    public void onEvent(int eventType, Bundle params) {}

}

