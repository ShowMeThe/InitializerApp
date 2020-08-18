package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType
import java.util.ArrayList

class TestInitializer : Initializer<String> {

    override fun onCreate(context: Context, isMainProcess: Boolean): String? {

        return TestSingle.instant.text
    }

    override fun dependencies(): List<Class<out Initializer<*>>>?{
        val list = ArrayList<Class<out Initializer<*>>>()
        list.add(TestInitializer2::class.java)
        return list
    }

    override fun initializerType(): InitializerType = InitializerType.Async
}