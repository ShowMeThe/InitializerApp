package com.android.myapplication

import android.content.Context
import com.show.launch.Initializer
import com.show.launch.InitializerType
import kotlinx.coroutines.CancellableContinuation
import kotlin.coroutines.resume

class TestInitializer3 : Initializer<String> {


    override fun onCreate(
        context: Context,
        isMainProcess: Boolean,
        continuation: CancellableContinuation<String>?
    ) {
        TestSingle.instant.text = "88888"
        continuation?.resume(TestSingle.instant.text)
    }

    override fun initializerType(): InitializerType = InitializerType.Async
}