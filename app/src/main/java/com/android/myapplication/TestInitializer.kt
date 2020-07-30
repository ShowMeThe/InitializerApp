package com.android.myapplication

import com.show.launch.Initializer

class TestInitializer : Initializer<Unit>() {

    override fun onCreateSync(): Unit? {

        return Unit
    }

    override suspend fun onCreateAsync(): Unit? {

        return Unit
    }

}