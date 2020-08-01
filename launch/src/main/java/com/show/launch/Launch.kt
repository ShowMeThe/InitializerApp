package com.show.launch

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import kotlinx.coroutines.*
import java.util.concurrent.Executors


class Launch{

    companion object{
        val instant by lazy { Launch() }
        fun attach(context: Context) = instant.apply {
            applicationCtx = context
        }
    }

    private val threadPool = Executors.newFixedThreadPool(2)
    private lateinit var applicationCtx: Context
    private val initialized = HashMap<Class<*>,Any?>()

    fun enableLog(): Launch{
        InitLog.enableLog = true
        return this
    }

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
    private fun doInitClazz(clazz:Class<out Initializer<*>>,initializing:HashSet<Class<*>>) {
        try {
            try {
                if(!initialized.contains(clazz)){
                    /**
                     * not contain
                     */
                    var result: Any?
                    val instant =  newInstant(clazz)
                    if(initializing.contains(clazz)){
                        return
                    }
                    initializing.add(clazz)
                    if(instant.initializerType() == InitializerType.Sync){
                        if(instant.dependencies()!=null){
                            val dependencies = instant.dependencies()!!
                            if(dependencies.isNotEmpty()){
                                for(depClazz in dependencies){
                                    if (!initialized.containsKey(depClazz)) {
                                        doInitClazz(depClazz, initializing)
                                    }
                                }
                            }
                        }
                        result = instant.onCreate(applicationCtx,applicationCtx.isMainProcess())
                        InitLog.log("SyncInitialized class: $instant  and result:$result ")
                        initialized[clazz] = result
                        initializing.remove(clazz)
                    }else{
                        GlobalScope.launch(Dispatchers.Default){
                            withContext(Dispatchers.Default){
                               if(instant.dependencies()!=null){
                                   val dependencies = instant.dependencies()!!
                                   if(dependencies.isNotEmpty()){
                                       for(depClazz in dependencies){
                                           if (!initialized.containsKey(depClazz)) {
                                               initializing.add(depClazz)
                                               val innerInstant = newInstant(depClazz)
                                               val out = innerInstant.onCreate(applicationCtx,applicationCtx.isMainProcess())
                                               InitLog.log("AsyncInitialized class: $innerInstant  and result:$out ")
                                               initialized[depClazz] = out
                                               initializing.remove(depClazz)
                                           }
                                       }
                                   }
                                   do {
                                       delay(10)
                                   }while (!isAsyncInitialized(dependencies))
                               }
                            }
                            initializing.add(clazz)
                            result = instant.onCreate(applicationCtx,applicationCtx.isMainProcess())
                            InitLog.log("AsyncInitialized class: $instant  and result:$result ")
                            initialized[clazz] = result
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

    private fun newInstant(clazz: Class<out Initializer<*>>) = clazz.getDeclaredConstructor().newInstance()


    private fun isAsyncInitialized(clazz: List<Class<out Initializer<*>>>):Boolean{
        var isInit = true
        for(dep in clazz){
            isInit = if(initialized.containsKey(dep)){
                isInit
            }else{
                false
            }
        }
        return isInit
    }


}