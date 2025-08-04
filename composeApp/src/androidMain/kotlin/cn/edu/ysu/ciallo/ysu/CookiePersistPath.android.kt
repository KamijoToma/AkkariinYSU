package cn.edu.ysu.ciallo.ysu

import android.content.Context

private var appContext: Context? = null

fun initCookiePersistPath(context: Context) {
    appContext = context.applicationContext
}

actual fun getCookiePersistPath(): String {
    val ctx = appContext ?: error("请先调用initCookiePersistPath(context)")
    return ctx.filesDir.absolutePath + "/cookies.json"
}