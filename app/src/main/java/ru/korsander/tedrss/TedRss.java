package ru.korsander.tedrss;

import android.app.Application;

/**
 * Created by korsander on 06.05.2015.
 */
public class TedRss extends Application{
    private static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getApplicationContext() {
        return context;
    }
}
