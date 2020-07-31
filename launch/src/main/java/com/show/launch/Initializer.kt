package com.show.launch

import android.content.Context

open class Initializer<T> {


   open fun onCreateSync(context: Context,isMainProcess:Boolean)  : T?{
       return null
   }

    open suspend fun onCreateAsync(context: Context,isMainProcess:Boolean) : T?{
        return null
    }

    open fun dependencies() : ArrayList<Class<out  Initializer<*>>>?{
        return null
    }

    open fun initializerType() = InitializerType.Sync
}