package com.android.myapplication

import android.content.Context
import android.util.Log
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.CancellableContinuation
import java.util.ArrayList
import kotlin.coroutines.resume

class TestInitializer : Initializer<String> {

    override fun dependencies(): List<Class<out Initializer<*>>>? {
        return ArrayList<Class<out Initializer<*>>>().apply {
            add(TestInitializer2::class.java)
        }
    }

    override fun onCreate(context: Context, isMainProcess: Boolean, continuation: CancellableContinuation<String>?) {

    }

    override fun initializerType(): InitializerType = InitializerType.Sync

}