package com.example.mbigelli.watsonchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by mbigelli on 24/04/17.
 */

public class OrchestrationTask extends AsyncTask<JSONObject, Void, Void> {

    private WeakReference<OrchestrationListener> listenerWeakReference;
    private String username;
    private String password;
    private Boolean useConversation;
    private String workspace;
    private String customURL;

    public OrchestrationTask(OrchestrationListener listener, Context context) {
        this.listenerWeakReference = new WeakReference(listener);
        listenerWeakReference.get().onQueryStart();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        username = prefs.getString("orchestration_username", "");
        password = prefs.getString("orchestration_password", "");
        useConversation = prefs.getBoolean("orchestration_use_conversation", true);
        workspace = prefs.getString("orchestration_workspace", "");
        customURL = prefs.getString("orchestration_custom_url", "");
    }

    @Override
    protected Void doInBackground(JSONObject... params) {
        try {
            // Connection setup
            HttpURLConnection conn = (HttpURLConnection) requestURL().openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", RestUtils.authString(username, password));
            conn.setRequestProperty("Content-type", "application/json");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            // Post content
            OutputStream outputStream = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            writer.write(params[0].toString());
            writer.flush();
            writer.close();
            outputStream.close();

            // Get output
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String result = RestUtils.readStream(conn.getInputStream());
                JSONObject resultJSON = new JSONObject(result);
                String answer = resultJSON.getJSONObject("output").getJSONArray("text").join(" ");
                JSONObject context = resultJSON.getJSONObject("context");
                listenerWeakReference.get().onQuerySuccess(answer, context);
            } else {
                listenerWeakReference.get().onQueryFailure("Server responded with " + conn.getResponseCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            listenerWeakReference.get().onQueryFailure(e.toString());
        }
        return null;
    }

    public void cancel() {
        cancel(true);
    }

    @Nullable
    private URL requestURL() {
        try {
            if (useConversation) {
                return new URL("https://gateway.watsonplatform.net/conversation/api/v1/workspaces/"+workspace+"/message?version=2017-04-21");
            } else {
                return new URL(customURL);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}

