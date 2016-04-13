package com.project.flyingchess;

import android.app.Application;

import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.Logger;

/**
 * Created by Administrator on 2016/4/10.
 */
public class App extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        Logger
            .init("FlyingChess")     // default PRETTYLOGGER or use just init()
            .methodCount(0)                 // default 2
            .hideThreadInfo()               // default shown
            //.logLevel(LogLevel.NONE)        // default LogLevel.FULL
            //.methodOffset(0)                // default 0
            .logTool(new AndroidLogTool()); // custom log tool, optional    }
    }
}
