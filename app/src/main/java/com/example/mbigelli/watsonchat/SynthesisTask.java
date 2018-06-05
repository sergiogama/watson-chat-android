package com.example.mbigelli.watsonchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.lang.ref.WeakReference;
import java.util.Map;


/**
 * Created by mbigelli on 04/05/17.
 */

public class SynthesisTask extends AsyncTask< Map<String, String>, Void, Void > {

    WeakReference<SynthesisListener> listenerWeakReference;
    Context context;
    private String username;
    private String password;
    private String voice;
    MediaPlayer mediaPlayer;

    public SynthesisTask(SynthesisListener listener, Context context) {
        this.listenerWeakReference = new WeakReference(listener);
        this.context = context;
        listenerWeakReference.get().onSynthesisStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString("voice_synthesis_username", "");
        password = prefs.getString("voice_synthesis_password", "");
        voice = prefs.getString("voice_synthesis_voice", "");
    }

    @Override
    protected Void doInBackground(Map<String, String>... params) {
        try {
            params[0].put("voice", voice);
            Uri uri = RestUtils.uriWithParams("https://stream.watsonplatform.net/text-to-speech/api/v1/synthesize", params[0]);
            Map<String, String> headers = RestUtils.authHeaders(username, password);

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(context, uri, headers);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    listenerWeakReference.get().onSpeechSuccess();
                    mp.release();
                }

            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    listenerWeakReference.get().onSynthesisError("Error");
                    mp.release();
                    return false;
                }
            });
            mediaPlayer.prepare(); // This will block the thread for a while (synthesizing)
            mediaPlayer.start();
            listenerWeakReference.get().onSpeechStart();
        } catch (Exception e) {
            e.printStackTrace();
            listenerWeakReference.get().onSynthesisError("Error");
        }
        return null;
    }

    public void cancel() {
        cancel(true);
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}