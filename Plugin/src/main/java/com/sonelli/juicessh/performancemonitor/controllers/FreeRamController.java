package com.sonelli.juicessh.performancemonitor.controllers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.sonelli.juicessh.performancemonitor.R;
import com.sonelli.juicessh.pluginlibrary.exceptions.ServiceNotConnectedException;
import com.sonelli.juicessh.pluginlibrary.listeners.OnSessionExecuteListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FreeRamController extends PercentageController {

    public static final String TAG = "FreeRamController";

    public FreeRamController(Context context) {
        super(context);
    }

    private int total = -1;
    private int free = -1;

    @Override
    public PercentageController start() {
        super.start();

        // Compile the regex patterns outside of the menu_main loop (cpu heavy)
        final Pattern totalPattern = Pattern.compile("^MemTotal:\\s*([0-9]+)");
        final Pattern freePattern = Pattern.compile("^MemFree:\\s*([0-9]+)");



        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {

                    getPluginClient().executeCommandOnSession(
                            getSessionId(),
                            getSessionKey(),
                            "cat /proc/meminfo",
                            new OnSessionExecuteListener() {

                        @Override
                        public void onCompleted(int exitCode) {
                            switch(exitCode){
                                case COMMAND_NOT_FOUND:
                                    setPercentage(0);
                                    Log.d(TAG, "Tried to run a command but the command was not found on the server");
                                    break;
                            }
                        }
                        @Override
                        public void onOutputLine(String line) {

                            Matcher totalMatcher = totalPattern.matcher(line);
                            Matcher freeMatcher = freePattern.matcher(line);

                            if (totalMatcher.find()){
                                total = Integer.valueOf(totalMatcher.group(1));
                            }

                            if(freeMatcher.find()){
                                free = Integer.valueOf(freeMatcher.group(1));
                            }


                            int percentageUsed = ((total - free) * 100) / total ;
                            if (getPercentage() != percentageUsed) {
                                setPercentage(percentageUsed);
                            }

                        }

                        @Override
                        public void onError(int error, String reason) {
                            toast(reason);
                        }

                    });
                } catch (ServiceNotConnectedException e){
                    Log.d(TAG, "Tried to execute a command but could not connect to JuiceSSH plugin service");
                }

                if(isRunning()){
                    handler.postDelayed(this, POLLING_INTERVAL * 1000L);
                }
            }
        });
        return this;
    }

    @Override
    public void stop() {
        total = -1;
        free = -1;
        super.stop();
    }

}
