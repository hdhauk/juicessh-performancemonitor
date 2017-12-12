package com.sonelli.juicessh.performancemonitor.controllers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.sonelli.juicessh.performancemonitor.R;
import com.sonelli.juicessh.pluginlibrary.exceptions.ServiceNotConnectedException;
import com.sonelli.juicessh.pluginlibrary.listeners.OnSessionExecuteListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiskUsageController extends PercentageController {

    public static final String TAG = "DiskUsageController";

    public DiskUsageController(Context context) {
        super(context);
    }

    @Override
    public PercentageController start() {
        super.start();

        // Work out the free disk space percentage on the / disk

        final Pattern diskUsagePattern = Pattern.compile("([0-9].)+%"); // Heavy cpu so do out of loops.

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {

                try {
                    getPluginClient().executeCommandOnSession(getSessionId(), getSessionKey(), "df | grep ' /$'", new OnSessionExecuteListener() {
                        @Override
                        public void onCompleted(int exitCode) {
                            switch(exitCode){
                                case COMMAND_NOT_FOUND:
                                    setPercentage(0);
                                    toast("Command df or grep not found on server.");
                                    Log.d(TAG, "Tried to run a command but the command was not found on the server");
                                    break;
                            }
                        }
                        @Override
                        public void onOutputLine(String line) {
                            Matcher diskUsageMatcher = diskUsagePattern.matcher(line);
                            if(diskUsageMatcher.find()){
                                setPercentage(Integer.valueOf(diskUsageMatcher.group(1)));
                            }
                        }

                        @Override
                        public void onError(int error, String reason) {
                            toast(reason);
                        }
                    });
                } catch (ServiceNotConnectedException e) {
                    Log.d(TAG, "Tried to execute a command but could not connect to JuiceSSH plugin service");
                }

                if(isRunning()){
                    handler.postDelayed(this, POLLING_INTERVAL * 1000L);
                }
            }


        });
        return this;

    }

}
