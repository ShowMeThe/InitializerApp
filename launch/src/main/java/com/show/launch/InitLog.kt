package com.show.launch

import android.util.Log


internal object InitLog {

    fun log(text:String){
        if(BuildConfig.DEBUG){
            Log.d("InitLog",text)
        }
    }
}