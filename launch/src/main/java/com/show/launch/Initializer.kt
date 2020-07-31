package com.show.launch

import android.content.Context

interface Initializer<T> {

     fun onCreate(context: Context, isMainProcess:Boolean)  : T?

     fun dependencies() : List<Class<out Initializer<*>>>? = null

     fun initializerType() = InitializerType.Sync
}