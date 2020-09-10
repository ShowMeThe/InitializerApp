package com.android.myapplication

class TestSingle {

    companion object{
        val instant by lazy { TestSingle() }
    }

    var text = "5555"

}