package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType

class TestInitializer3 : Initializer<String> {

    override fun onCreate(context: Context, isMainProcess: Boolean): String? {
        Thread.sleep(500)
        TestSingle.instant.text = "88888"
        return TestSingle.instant.text
    }

    override fun initializerType(): InitializerType = InitializerType.Async
}