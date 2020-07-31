package com.android.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.show.launch.Launch

class MyService : Service() {

    override fun onCreate() {
        super.onCreate()
        Log.e("2222222222","${Launch.instant}")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}