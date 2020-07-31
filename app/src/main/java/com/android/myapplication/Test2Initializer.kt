package com.android.myapplication

import android.content.Context
import android.util.Log
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.delay

class Test2Initializer: Initializer<TestSingle>() {

    override suspend fun onCreateAsync(context: Context, isMainProcess: Boolean): TestSingle? {
        TestSingle.instant.text = "123121213"
        Log.e("2222222222","Test2Initializer do onCreateAsync ${TestSingle.instant.text}")
        return TestSingle.instant
    }

    override fun dependencies(): ArrayList<Class<out Initializer<*>>>? {
        return null
    }

    override fun initializerType(): InitializerType = InitializerType.Async
}