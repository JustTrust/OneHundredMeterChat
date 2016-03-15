package org.belichenko.a.onehundredmeterchat;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MsgService extends Service {

    MyBinder binder = new MyBinder();

    public MsgService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyBinder extends Binder {
        MsgService getService() {
            return MsgService.this;
        }
    }
}
