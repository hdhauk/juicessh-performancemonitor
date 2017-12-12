package com.sonelli.juicessh.performancemonitor.controllers;


import android.content.Context;

import android.os.Handler;
import android.util.Log;

import com.sonelli.juicessh.pluginlibrary.exceptions.ServiceNotConnectedException;
import com.sonelli.juicessh.pluginlibrary.listeners.OnSessionExecuteListener;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UpTimeController extends TextController {


    public static final String TAG = "UpTimeController";

    public UpTimeController(Context context) { super(context); }

    @Override
    public TextController start() {
        super.start();

        final Pattern uptimePattern = Pattern.compile("(^\\d*.\\d*)\\s*(\\d*.\\d*)");

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    getPluginClient().executeCommandOnSession(
                            getSessionId(),
                            getSessionKey(),
                            "cat /proc/uptime",
                            new OnSessionExecuteListener() {
                                private float upTimeInSec = -1;
                                private float idleTimeInSec = -1;

                                @Override
                                public void onCompleted(int exitCode) {
                                    int commandNotFound = 127;
                                    if (exitCode == commandNotFound) {
                                        setText("Error");
                                        Log.d(TAG, "command cat not found");
                                    }
                                }

                                @Override
                                public void onOutputLine(String line) {
                                    Matcher uptimeMatcher = uptimePattern.matcher(line);
                                    if (uptimeMatcher.find()) {
                                        upTimeInSec   = Float.parseFloat(uptimeMatcher.group(1));
                                        idleTimeInSec = Float.parseFloat(uptimeMatcher.group(2));
                                        setText(SecondsToHumanReadable(upTimeInSec));

                                    }
                                }

                                @Override
                                public void onError(int error, String reason) { toast(reason);}
                            }
                    );
                } catch (ServiceNotConnectedException e) {
                    Log.d(TAG, "Tried to execute a command, " +
                            "but could not connect to JuiceSSH plugin service");
                }

                if ( isRunning() ) {
                    handler.postDelayed(this, INTERVAL_SECONDS * 1000L);
                }

            }
        });
        return this;
    }

    public String SecondsToHumanReadable(float seconds) {
        long secs = (long) seconds;

        long days = secs / (60*60*24);
        secs = secs % (60*60*24);

        long hrs = secs / (60*60);
        secs = secs % (60*60);

        long mins = secs / 60;
        secs = secs % 60;


        return String.format(Locale.US, "%dD %dH\n%dM %dS", days, hrs, mins, secs);
    }

}
