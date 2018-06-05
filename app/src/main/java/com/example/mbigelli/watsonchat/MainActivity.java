package com.example.mbigelli.watsonchat;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends Activity implements WatsonListener {

    private String idleGif = "watson_idle.gif";
    private String listenGif = "watson_listen.gif";
    private String thinkGif = "watson_think.gif";
    private String speakGif = "watson_speak.gif";
    private String currentGif = "watson_idle.gif";
    private TextView stateTextView;
    private Button settingsButton;

    private WebView webView;

    private Watson watson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();

        stateTextView = (TextView) findViewById(R.id.textView);
        webView = (WebView) findViewById(R.id.webView);
        settingsButton = (Button) findViewById(R.id.settingsButton);

        watson = new Watson(this, getBaseContext());

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (watson.state == WatsonState.IDLE) {
                        watson.startListening();
                    } else {
                        watson.stop();
                    }
                }
                return false;
            }
        });
        Intent detailsIntent =  new Intent(RecognizerIntent.ACTION_GET_LANGUAGE_DETAILS);
        sendOrderedBroadcast(
                detailsIntent, null, new LanguageDetailsChecker(), null, Activity.RESULT_OK, null, null);
    }

    void updateWebView() {
        Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        String style = "";
        if (display.getRotation() % 2 == 0) {
            style = "style=\"width = 100%;\"";
        } else {
            style = "style=\"height = 100%;\"";
        }
        String htmlString = "<body style=\"height:100%;width:100%;padding:0;margin:0;background-color:black;text-align:center;\">" +
                "<img src=\"" + currentGif + "\"" + style + "></img>" +
                "</body>";
        webView.loadDataWithBaseURL("file:///android_asset/", htmlString, "text/html", "utf-8", null);
    }

    void requestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECORD_AUDIO }, 0);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    // WatsonListener
    @Override
    public void didChangeState(final WatsonState newState) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                switch (newState) {
                    case IDLE:
                        currentGif = idleGif;
                        stateTextView.setText("Tap to speak!");
                        break;
                    case LISTENING:
                        currentGif = listenGif;
                        stateTextView.setText("Listening...");
                        break;
                    case QUERYING:
                        currentGif = thinkGif;
                        stateTextView.setText("Classifying...");
                        break;
                    case SYNTHESIZING:
                        currentGif = thinkGif;
                        stateTextView.setText("Synthesizing...");
                        break;
                    case SPEAKING:
                        currentGif = speakGif;
                        stateTextView.setText("Speaking...");
                        break;
                }
                updateWebView();
            }
        });
    }

    @Override
    public void didFail(String module, String description) {

    }

//    func didFail(module: String, description: String) {
//        UIHelper.simpleAlert(title: module + " error", text: description, owner: self)
//    }
}

class LanguageDetailsChecker extends BroadcastReceiver
{
    private List<String> supportedLanguages;

    private String languagePreference;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle results = getResultExtras(true);
        if (results.containsKey(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE)) {
            languagePreference = results.getString(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE);
        }
        if (results.containsKey(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES)) {
            supportedLanguages = results.getStringArrayList(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES);
        }
        Log.v("LANGUAGES", languagePreference);
        Log.v("LANGUAGES", supportedLanguages.toString());
    }
}