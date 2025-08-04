package cn.edu.ysu.ciallo.ysu

import java.io.File
import java.nio.file.Paths

actual fun getCookiePersistPath(): String {
    val programDir = File(".").absoluteFile
    val programPath = File(programDir, "ysu_cookies.json")
    return if (canWriteFile(programPath)) {
        programPath.absolutePath
    } else {
        val userHome = System.getProperty("user.home")
        Paths.get(userHome, ".ysu_cookies.json").toString()
    }
}

private fun canWriteFile(file: File): Boolean {
    return try {
        if (!file.exists()) file.createNewFile()
        val canWrite = file.canWrite()
        if (!file.exists()) file.delete() // 清理测试文件
        canWrite
    } catch (e: Exception) {
        false
    }
}