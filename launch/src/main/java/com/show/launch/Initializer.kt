package com.show.launch

abstract class Initializer<T> {


    abstract fun onCreateSync()  : T?

    abstract  suspend fun onCreateAsync() : T?

}