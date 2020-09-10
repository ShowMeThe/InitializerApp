package com.show.launch

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_META_DATA
import android.util.Log
import kotlinx.coroutines.*
import java.util.ArrayList
import java.util.concurrent.Executors


class Launch {

    companion object {
        val instant by lazy { Launch() }
        fun attach(context: Context) = instant.apply {
            applicationCtx = context
        }
    }

    private lateinit var applicationCtx: Context
    private val initialized = HashMap<Class<*>, Any?>()
    private val componentClazz = HashSet<Class<out Initializer<*>>>()

    fun enableLog(): Launch {
        InitLog.enableLog = true
        return this
    }

    fun addComponent(vararg clazz: Class<out Initializer<*>>): Launch {
        componentClazz.addAll(clazz)
        return this
    }

    fun doInit() {
        try {
            val provide = ComponentName(applicationCtx.packageName, AppProvider::class.java.name)
            val providerInfo = applicationCtx.packageManager.getProviderInfo(provide, GET_META_DATA)
            val initializerName = applicationCtx.getString(R.string.initializer)
            val metadata = providerInfo.metaData
            val set = HashSet<Class<*>>()
            if (metadata != null) {
                GlobalScope.launch(Dispatchers.IO) {
                    withTimeout(60 * 1000) {
                        val keys = metadata.keySet()
                        for (key in keys) {
                            val dataValue = metadata.getString(key, null)
                            if (dataValue == initializerName) {
                                val clazz = Class.forName(key)
                                if (Initializer::class.java.isAssignableFrom(clazz)) {
                                    doInitClazz(clazz as Class<out Initializer<*>>, set)
                                }
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

    @Synchronized
    private suspend fun doInitClazz(
        clazz: Class<out Initializer<*>>,
        initializing: HashSet<Class<*>>
    ) {
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
                                        doInitClazz(depClazz, initializing)
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