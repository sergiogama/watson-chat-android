package com.example.mbigelli.watsonchat;

import org.json.JSONObject;

public interface OrchestrationListener {
    void onQueryStart();
    void onQuerySuccess(String answer, JSONObject context);
    void onQueryFailure(String message);
}
