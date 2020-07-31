package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType

class TestInitializer2 : Initializer<String> {

    override fun onCreate(context: Context, isMainProcess: Boolean): String? {
        Thread.sleep(5000)
        TestSingle.instant.text = "55555"
        return TestSingle.instant.text
    }

    override fun initializerType(): InitializerType = InitializerType.Async
}