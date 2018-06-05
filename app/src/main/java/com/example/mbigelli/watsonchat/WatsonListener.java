package com.example.mbigelli.watsonchat;

public interface WatsonListener {
    void didChangeState(WatsonState newState);
    void didFail(String module, String description);
}
