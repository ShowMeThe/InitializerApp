package com.show.launch

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Launch private constructor() {

    companion object {
        private val instant by lazy { Launch() }
        fun launch(context: Context): Launch = instant.apply {
            applicationCtx = context.applicationContext
        }

    }

    private lateinit var applicationCtx: Context
    private val initializerSet = HashMap<Class<*>,Any?>()

    fun doInit(){
        try {
            val provide = ComponentName(applicationCtx.packageName,AppProvider::class.java.name)
            val providerInfo = applicationCtx.packageManager.getProviderInfo(provide,GET_META_DATA)
            val initializerName = applicationCtx.getString(R.string.initializer)
            val metadata = providerInfo.metaData
            if(metadata!=null){
                val set = HashSet<Class<*>>()
                val keys = metadata.keySet()
                for(key in keys){
                    val dataValue = metadata.getString(key,null)
                    if(dataValue == initializerName){
                        val clazz = Class.forName(key)
                        if(Initializer::class.java.isAssignableFrom(clazz)){
                            doInitClazz(clazz as Class<Initializer<*>>,set)
                        }
                    }
                }
            }
        }catch (e: PackageManager.NameNotFoundException){
            e.printStackTrace()
        }catch (e: ClassNotFoundException ){
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun doInitClazz(clazz:Class<Initializer<*>>,set:HashSet<Class<*>>){
        if(!initializerSet.contains(clazz)){
            /**
             * not contain
             */
            set.add(clazz)

            var result : Any? = null
            val instant = clazz.getDeclaredConstructor().newInstance()
            if(instant.onCreateSync() != null){
                  result = instant.onCreateSync()
                 initializerSet[clazz] = result
                 set.remove(clazz)
            }else{
                GlobalScope.launch(Dispatchers.IO) {
                    result = instant.onCreateAsync()
                    initializerSet[clazz] = result
                    set.remove(clazz)
                }
            }
        }
    }

}