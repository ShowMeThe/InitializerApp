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
    private val initializedSet = HashMap<Class<*>,Any?>()

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
                            doInitClazz(clazz as Class<out Initializer<*>>,set)
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
    private fun doInitClazz(clazz:Class<out Initializer<*>>,initializing:HashSet<Class<*>>){
        try {
            try {
                if(!initializedSet.contains(clazz)){
                    /**
                     * not contain
                     */
                    var result: Any?
                    val instant =  clazz.getDeclaredConstructor().newInstance()
                    if(instant.dependencies()!=null){
                        val dependencies = instant.dependencies()!!
                        if(dependencies.isNotEmpty()){
                            for(depClazz in dependencies){
                                if (!initializedSet.containsKey(depClazz)) {
                                    doInitClazz(depClazz, initializing)
                                }
                            }
                        }
                    }

                    if(initializing.contains(clazz)){
                        return
                    }
                    initializing.add(clazz)
                    if(instant.initializerType() == InitializerType.Sync){
                        result = instant.onCreateSync(applicationCtx,applicationCtx.isMainProcess())
                        Log.e("2222222222","onCreateSync ${instant}  ${result}")
                        initializedSet[clazz] = result
                        initializing.remove(clazz)
                    }else{
                        GlobalScope.launch(Dispatchers.IO) {
                            initializing.add(clazz)
                            result = instant.onCreateAsync(applicationCtx,applicationCtx.isMainProcess())
                            initializedSet[clazz] = result
                            initializing.remove(clazz)
                        }
                    }
                }
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }finally {

        }
    }

}