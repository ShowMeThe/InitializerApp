package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.CancellableContinuation
import java.util.ArrayList
import kotlin.coroutines.resume

class TestInitializer2 : Initializer<String> {

    override fun dependencies(): List<Class<out Initializer<*>>>?{
        val list = ArrayList<Class<out Initializer<*>>>()
        list.add(TestInitializer3::class.java)
        return list
    }

    override fun onCreate(context: Context, isMainProcess: Boolean, continuation: CancellableContinuation<String>?) {

        TestSingle.instant.text = "${TestSingle.instant.text}55555"
        continuation?.resume(TestSingle.instant.text)
    }

    override fun initializerType(): InitializerType = InitializerType.Async

}