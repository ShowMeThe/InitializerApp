package com.show.launch

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.ArrayList
import java.util.concurrent.Executors


class Launch {

    companion object {
        private val instant by lazy { Launch() }
        fun get() = instant
        fun attach(context: Context) = instant.apply {
            applicationCtx = context
        }
    }

    private lateinit var applicationCtx: Context
    private val initialized = HashMap<Class<*>, Any?>()

    fun enableLog(): Launch {
        InitLog.enableLog = true
        return this
    }


    fun doInit() {
        try {
            val provide = ComponentName(applicationCtx.packageName, AppProvider::class.java.name)
            val providerInfo = applicationCtx.packageManager.getProviderInfo(provide, GET_META_DATA)
            val initializerName = applicationCtx.getString(R.string.initializer)
            val metadata = providerInfo.metaData
            val initializing = HashSet<Class<*>>()
            if (metadata != null) {
                val keys = metadata.keySet()
                for (key in keys) {
                    val dataValue = metadata.getString(key, null)
                    if (dataValue == initializerName) {
                        val clazz = Class.forName(key)
                        if (Initializer::class.java.isAssignableFrom(clazz)) {
                            val instant = newInstant(clazz as Class<out Initializer<*>>)
                            if(getDeepContainsAsync(instant)){
                                GlobalScope.launch(Dispatchers.IO) {
                                    withTimeout(60 * 1000) {
                                        doInitClazzAsync(clazz,initializing)
                                    }
                                }
                            }else{
                                doInitClazzSync(clazz,initializing)
                            }
                        }
                    }
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun getDeepContainsAsync(initializer: Initializer<*>):Boolean{
        when {
            initializer.initializerType() == InitializerType.Async -> {
                return true
            }
            initializer.dependencies() == null -> {
                return false
            }
            else -> {
                for(inside in initializer.dependencies()!!){
                    return getDeepContainsAsync(newInstant(inside))
                }
            }
        }
        return false
    }

    @Synchronized
    private fun doInitClazzSync(clazz: Class<out Initializer<*>>, initializing: HashSet<Class<*>>) {
        try {
            try {
                if (!initialized.contains(clazz)) {
                    /**
                     * not contain
                     */
                    val result: Any?
                    val instant = newInstant(clazz)
                    if (initializing.contains(clazz)) {
                        return
                    }
                    initializing.add(clazz)
                    if (!instant.dependencies().isNullOrEmpty()) {
                        val dependencies = instant.dependencies()!!
                        if (dependencies.isNotEmpty()) {
                            for (depClazz in dependencies) {
                                if (!initialized.containsKey(depClazz)) {
                                    doInitClazzSync(depClazz, initializing)
                                }
                            }
                        }
                    }
                    result = instant.onCreate(applicationCtx, applicationCtx.isMainProcess(), null)
                    InitLog.log("Initialized class : $instant  and result:$result ")
                    initialized[clazz] = result
                    initializing.remove(clazz)
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } finally {

        }
    }


    @Synchronized
    private suspend fun doInitClazzAsync(clazz: Class<out Initializer<*>>, initializing: HashSet<Class<*>>) {
        try {
            try {
                if (!initialized.contains(clazz)) {
                    /**
                     * not contain
                     */
                    var result: Any?
                    val instant = newInstant(clazz)
                    if (initializing.contains(clazz)) {
                        return
                    }
                    withContext(Dispatchers.Unconfined) {
                        initializing.add(clazz)
                        if (!instant.dependencies().isNullOrEmpty()) {
                            val dependencies = instant.dependencies()!!
                            if (dependencies.isNotEmpty()) {
                                for (depClazz in dependencies) {
                                    if (!initialized.containsKey(depClazz)) {
                                        doInitClazzAsync(depClazz, initializing)
                                    }
                                }
                            }
                        }
                        result = suspendCancellableCoroutine { continuation ->
                            instant.onCreate(
                                applicationCtx,
                                applicationCtx.isMainProcess(),
                                continuation
                            )
                        }
                        InitLog.log("Initialized class : $instant  and result:$result ")
                        initialized[clazz] = result
                        initializing.remove(clazz)
                    }

                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } finally {

        }
    }


    private fun newInstant(clazz: Class<out Initializer<*>>) =
        clazz.getDeclaredConstructor().newInstance()

}