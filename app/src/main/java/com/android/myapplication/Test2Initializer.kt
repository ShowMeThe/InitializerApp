package com.android.myapplication

import com.show.launch.Initializer
import kotlinx.coroutines.delay

class Test2Initializer: Initializer<String>() {
    override fun onCreateSync(): String? {
        return null
    }

    override suspend fun onCreateAsync(): String? {
         delay(200)
         return "1222231"
    }
}