package com.show.launch

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Process

/**
 * 判断是否主进程
 * @param context 上下文
 * @return true是主进程
 */
fun Context.isMainProcess(main:()->Unit) {
    if(isPidOfProcessName(this, getPid(), getMainProcessName(this))){
        main.invoke()
    }
}

/**
 * 判断该进程ID是否属于该进程名
 *
 * @param context
 * @param pid 进程ID
 * @param p_name 进程名
 * @return true属于该进程名
 */
private fun isPidOfProcessName(context: Context, pid: Int, p_name: String?): Boolean {
    if (p_name == null) return false
    var isMain = false
    val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (process in am.runningAppProcesses) {
        if (process.pid == pid) {
            if (process.processName == p_name) {
                isMain = true
            }
            break
        }
    }
    return isMain
}

/**
 * 获取主进程名
 * @param context 上下文
 * @return 主进程名
 */
@Throws(PackageManager.NameNotFoundException::class)
private fun getMainProcessName(context: Context): String? {
    return context.packageManager.getApplicationInfo(context.packageName, 0).processName
}

/**
 * 获取当前进程ID
 * @return 进程ID
 */
private fun getPid(): Int {
    return Process.myPid()
}