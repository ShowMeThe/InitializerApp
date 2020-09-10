package com.android.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.show.launch.Launch

class MyService : Service() {

    override fun onCreate() {
        super.onCreate()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}