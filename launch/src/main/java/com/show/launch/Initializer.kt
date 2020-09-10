package com.show.launch

import android.content.Context
import kotlinx.coroutines.CancellableContinuation

interface Initializer<T> {

     fun onCreate(context: Context, isMainProcess:Boolean,continuation : CancellableContinuation<T>?)

     fun dependencies() : List<Class<out Initializer<*>>>? = null

     fun initializerType() = InitializerType.Sync
}