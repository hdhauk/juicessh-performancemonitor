package com.sonelli.juicessh.performancemonitor.controllers;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.sonelli.juicessh.pluginlibrary.PluginClient;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TextController {

    public static final int INTERVAL_SECONDS = 2;

    private int sessionId;
    private String sessionKey;
    private PluginClient client;
    private TextView textView;
    private WeakReference<Context> context;

    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public TextController(Context context) {
        this.context = new WeakReference<>(context);
    }

    public TextController setSessionId(int sessionId){
        this.sessionId = sessionId;
        return this;
    }

    public TextController setSessionKey(String sessionKey){
        this.sessionKey = sessionKey;
        return this;
    }

    public TextController setPluginClient(PluginClient client){
        this.client = client;
        return this;
    }

    public TextController setTextview(TextView textView){
        this.textView = textView;
        return this;
    }

    public String getString(int resource){
        if(context.get() != null){
            return context.get().getString(resource);
        } else {
            return null;
        }
    }

    public int getSessionId() {
        return sessionId;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public PluginClient getPluginClient() {
        return client;
    }

    public void setText(String string){
        if(textView != null && !textView.getText().equals(string)){
            textView.setText(string);
        }
    }

    public boolean isRunning(){
        return isRunning.get();
    }

    public TextController start() {
        isRunning.set(true);
        return this;
    }

    public void stop(){
        isRunning.set(false);
    }

    public void toast(String reason) {
        if(context.get() != null){
            Toast.makeText(context.get(), reason, Toast.LENGTH_SHORT).show();
        }
    }

}
