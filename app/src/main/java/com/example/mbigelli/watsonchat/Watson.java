package com.example.mbigelli.watsonchat;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class Watson implements GenericSpeechRecognizerListener, OrchestrationListener, SynthesisListener {

    public WeakReference<WatsonListener> listenerWeakReference;
    private Context context;
    private GenericSpeechRecognizer genericSpeechRecognizer;
    private OrchestrationTask orchestrationTask;
    private SynthesisTask voiceSynthesizer;
    public WatsonState state;

    private JSONArray scheduledActions = new JSONArray();
    private String lastTranscription = "";
    private String lastAnswer = "";
    private JSONObject lastContext = new JSONObject();

    public Watson(WatsonListener listener, Context context) {
        this.listenerWeakReference = new WeakReference(listener);
        this.context = context;
        genericSpeechRecognizer = new NativeSpeechRecognizer(this, context);
        setState(WatsonState.IDLE);
    }

    // Interface methods
    public void startListening() {
        try {
            schedule(new JSONObject().put("type", "listen"));
            schedule(new JSONObject().put("type", "classify"));
            schedule(new JSONObject().put("type", "synthesize"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void mockQuestion(String question) {
        try {
            schedule(new JSONObject().put("type", "classify").put("text", question));
            schedule(new JSONObject().put("type", "synthesize"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        scheduledActions = new JSONArray();
        switch (state) {
            case IDLE:
                break;
            case LISTENING:
                genericSpeechRecognizer.cancel();
                break;
            case QUERYING:
                orchestrationTask.cancel();
                break;
            case SYNTHESIZING:
            case SPEAKING:
                voiceSynthesizer.cancel();
                break;
        }
        setState(WatsonState.IDLE);
    }

    public void setState(WatsonState newState) {
        state = newState;
        listenerWeakReference.get().didChangeState(newState);
        if (newState == WatsonState.IDLE) {
            nextAction();
        }
    }

    private void schedule(JSONObject action) {
        scheduledActions.put(action);
        if (state == WatsonState.IDLE) {
            nextAction();
        }
    }

    private void nextAction() {

        if (scheduledActions.length() == 0) {
            return;
        }

        try {
            final JSONObject action = scheduledActions.getJSONObject(0);
            scheduledActions = JSONUtils.remove(scheduledActions, 0);
            switch (action.getString("type")) {
                case "listen":
                    genericSpeechRecognizer.start();
                    break;
                case "classify":
                    orchestrationTask = new OrchestrationTask(this, context);
                    JSONObject body = new JSONObject();
                    JSONObject input = new JSONObject();
                    input.put("text", action.has("text") ? action.getString("text") : lastTranscription);
                    body.put("input", input);
                    body.put("context", lastContext);
                    orchestrationTask.execute(body);
                    break;
                case "synthesize":
                    voiceSynthesizer = new SynthesisTask(this, context);
                    HashMap<String, String> params = new HashMap<>();
                    params.put("text", lastAnswer);
                    voiceSynthesizer.execute(params);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        // GenericSpeechRecognizerListener methods
    @Override
    public void onRecognitionStart() {
        Log.v("Speech","Start");
        setState(WatsonState.LISTENING);
    }

    @Override
    public void onRecognitionSuccess(String result) {
        Log.v("Speech", result);
        lastTranscription = result;
        setState(WatsonState.IDLE);
    }

    @Override
    public void onRecognitionError(String message) {
        Log.v("Speech","Error");
        stop();
        listenerWeakReference.get().didFail("Speech Recognizer", message);
    }

    // OrchestrationListener methods
    @Override
    public void onQueryStart() {
        setState(WatsonState.QUERYING);
    }

    @Override
    public void onQuerySuccess(String result, JSONObject context) {
        Log.v("Conversation", result);
        lastAnswer = result;
        lastContext = context;
        setState(WatsonState.IDLE);
    }

    @Override
    public void onQueryFailure(String message) {
        Log.v("Conversation", message);
        stop();
        listenerWeakReference.get().didFail("OrchestrationTask", message);
    }



    @Override
    public void onSynthesisStart() {
        Log.v("Synthesis", "Start");
        setState(WatsonState.SYNTHESIZING);
    }

    @Override
    public void onSynthesisSuccess() {
        Log.v("Synthesis", "End");
    }

    // AudioSpeakerListener methods
    @Override
    public void onSpeechStart() {
        Log.v("Audio", "Start");
        setState(WatsonState.SPEAKING);
    }

    @Override
    public void onSpeechSuccess() {
        setState(WatsonState.IDLE);
    }

    @Override
    public void onSynthesisError(String message) {
        Log.v("Synthesis", message);
    }
}
