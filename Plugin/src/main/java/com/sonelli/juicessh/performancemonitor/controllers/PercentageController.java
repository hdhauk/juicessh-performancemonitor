package com.sonelli.juicessh.performancemonitor.controllers;


import android.content.Context;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.sonelli.juicessh.pluginlibrary.PluginClient;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class PercentageController {

    public static final int POLLING_INTERVAL = 2;
    public static final int COMMAND_NOT_FOUND = 127;

    private int sessionId;
    private String sessionKey;
    private PluginClient client;
    private ArcProgress arcProgress;
    private WeakReference<Context> ctx;
    private AtomicBoolean isRunning = new AtomicBoolean(false);

    public PercentageController(Context ctx) { this.ctx = new WeakReference<Context>(ctx); }

    public int getSessionId() { return sessionId; }

    public PercentageController setSessionId(int id){
        this.sessionId = id;
        return this;
    }
    public String getSessionKey() { return sessionKey; }

    public PercentageController setSessionKey(String key){
        this.sessionKey = key;
        return this;
    }
    public PluginClient getPluginClient() { return  client; }

    public PercentageController setPluginClient(PluginClient client){
        this.client = client;
        return this;
    }

    public PercentageController setArcProgress(ArcProgress arc){
        this.arcProgress = arc;
        return this;
    }

    public int getPercentage() { return arcProgress.getProgress(); }
    public void setPercentage(int percentage) {
        Boolean newValue = arcProgress.getProgress() != percentage;
        Boolean validValue = (percentage <= 100 && percentage >= 0);
        if (arcProgress != null && newValue && validValue){
            arcProgress.setProgress(percentage);
        }
    }

    public String getString(int resource){
        if(ctx.get() != null){
            return ctx.get().getString(resource);
        } else {
            return null;
        }
    }

    public boolean isRunning() { return isRunning.get(); }

    public PercentageController start() {
        isRunning.set(true);
        return this;
    }

    public void stop() { isRunning.set(false); }

    public void toast(String reason) {
        if(ctx.get() != null){
            Toast.makeText(ctx.get(), reason, Toast.LENGTH_SHORT).show();
        }
    }

}
