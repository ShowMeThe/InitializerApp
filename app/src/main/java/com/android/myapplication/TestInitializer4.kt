package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.CancellableContinuation
import java.util.ArrayList
import kotlin.coroutines.resume

class TestInitializer4 : Initializer<String> {

    override fun onCreate(
        context: Context,
        isMainProcess: Boolean,
        continuation: CancellableContinuation<String>?
    ) {

        TestSingle.instant.text = "88888"
        continuation?.resume(TestSingle.instant.text)
    }

    override fun dependencies(): List<Class<out Initializer<*>>>?{
        return ArrayList<Class<out Initializer<*>>>().apply {
            add(TestInitializer3::class.java)
        }
    }

    override fun initializerType(): InitializerType = InitializerType.Sync
}