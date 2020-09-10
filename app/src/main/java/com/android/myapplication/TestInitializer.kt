package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import kotlinx.coroutines.CancellableContinuation
import java.util.ArrayList
import kotlin.coroutines.resume

class TestInitializer : Initializer<String> {

    override fun onCreate(context: Context, isMainProcess: Boolean, continuation: CancellableContinuation<String>) {
        continuation.resume(TestSingle.instant.text)
    }
    override fun dependencies(): List<Class<out Initializer<*>>>?{
        val list = ArrayList<Class<out Initializer<*>>>()
        list.add(TestInitializer2::class.java)
        return list
    }

}