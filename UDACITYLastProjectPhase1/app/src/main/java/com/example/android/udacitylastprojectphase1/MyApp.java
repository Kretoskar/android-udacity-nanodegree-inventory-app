package com.example.android.udacitylastprojectphase1;

import android.app.Application;
import android.content.Context;

/**
 * Created by admin on 16.04.2018.
 */

public class MyApp extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        super.onCreate();
    }

    public static Context getContext() {
        return mContext;
    }
}
