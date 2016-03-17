package org.belichenko.a.onehundredmeterchat;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

/**
 * For getting context
 */
public class App extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
        // TODO: 23.02.2016 comment when developing is over
        Stetho.initializeWithDefaults(this);
    }

    public static Context getAppContext() {
        return App.context;
    }
}
