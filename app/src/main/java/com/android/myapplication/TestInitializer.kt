package com.android.myapplication

import android.content.Context
import android.util.Log
import com.show.launch.Initializer
import com.show.launch.InitializerType

class TestInitializer : Initializer<String>() {

    override suspend fun onCreateAsync(context: Context, isMainProcess: Boolean): String? {
        Log.e("2222222222","TestInitializer do onCreateAsync ${TestSingle.instant.text}")
        return TestSingle.instant.text
    }


    override fun dependencies(): ArrayList<Class<out Initializer<*>>>? {
        return arrayListOf(Test2Initializer::class.java)
    }

    override fun initializerType(): InitializerType = InitializerType.Async

}