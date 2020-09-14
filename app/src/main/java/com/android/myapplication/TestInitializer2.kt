package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.CancellableContinuation
import java.util.ArrayList
import kotlin.coroutines.resume

class TestInitializer2 : Initializer<String> {

    override fun dependencies(): List<Class<out Initializer<*>>>?{
        return ArrayList<Class<out Initializer<*>>>().apply {
            add(TestInitializer3::class.java)
        }
    }

    override fun onCreate(context: Context, isMainProcess: Boolean, continuation: CancellableContinuation<String>?) {
        Thread.sleep(5000)
        TestSingle.instant.text = "${TestSingle.instant.text}55555"
        continuation?.resume(TestSingle.instant.text)
    }

    override fun initializerType(): InitializerType = InitializerType.Sync

}