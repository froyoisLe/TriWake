package site.isleti.triwake.util;

import android.app.Application;

public class TriWakeApplication extends Application {

    private static TriWakeApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static TriWakeApplication getInstance(){
        return instance;
    }
}
