package cn.edu.ysu.ciallo.ysu

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.Url
import io.ktor.http.fullPath
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * 自定义Cookie存储，支持线程安全和过期清理。
 */
@Serializable
data class SerializableCookie(
    val name: String,
    val value: String,
    val expires: Long? = null,
    val domain: String? = null,
    val path: String? = null,
    val secure: Boolean = false,
    val httpOnly: Boolean = false
)

@Serializable
data class SerializableUrl(val protocol: String, val host: String, val port: Int, val path: String)

@Serializable
data class CookiePersistModel(val cookies: List<Pair<SerializableUrl, SerializableCookie>>)

class CustomCookieStorage : CookiesStorage {
    private val mutex = Mutex()
    private val cookieJar = mutableListOf<Pair<Url, Cookie>>()
    private val persistFile = getCookiePersistPath() // 平台适配路径

    init {
        loadFromDisk()
    }

    private fun Cookie.toSerializable(): SerializableCookie = SerializableCookie(
        name, value, expires?.timestamp, domain, path, secure, httpOnly
    )
    private fun SerializableCookie.toCookie(): Cookie = Cookie(
        name, value, CookieEncoding.URI_ENCODING, 0, expires?.let { GMTDate(it) }, domain, path, secure, httpOnly
    )
    private fun Url.toSerializable(): SerializableUrl = SerializableUrl(protocol.name, host, port, fullPath)
    private fun SerializableUrl.toUrl(): Url = Url("$protocol://$host${if (port != 0) ":$port" else ""}$path")

    fun saveToDisk() {
        val serializable = cookieJar.map { (url, cookie) -> url.toSerializable() to cookie.toSerializable() }
        val json = Json.encodeToString(CookiePersistModel(serializable))
        try {
            File(persistFile).writeText(json)
        } catch (e: Exception) {
            println("保存Cookie失败: ${e.message}")
        }
    }

    private fun loadFromDisk() {
        try {
            val file = File(persistFile)
            if (!file.exists()) return
            val json = file.readText()
            val model = Json.decodeFromString<CookiePersistModel>(json)
            cookieJar.clear()
            cookieJar.addAll(model.cookies.map { it.first.toUrl() to it.second.toCookie() })
        } catch (e: Exception) {
            println("加载Cookie失败: ${e.message}")
        }
    }

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            if (cookie.name.equals("CASTGC", ignoreCase = true)) {
                // if CASTGC cookie is present in cookieJar and its value is not empty, return
                if (cookie.value.isBlank() && cookieJar.any { (_, c) -> /*url.host == requestUrl.host &&*/ c.name.equals("CASTGC", ignoreCase = true) && c.value.isNotEmpty() }) {
                    println("CASTGC cookie already exists with a non-empty value, not adding again.")
                    return
                }
            }
            // 移除同名cookie
            cookieJar.removeAll { (url, c) -> url.host == requestUrl.host && c.name == cookie.name }
            cookieJar.add(requestUrl to cookie)
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> = mutex.withLock {
        val now = System.currentTimeMillis() / 1000
        cookieJar.filter { (url, cookie) ->
            url.host == requestUrl.host && (cookie.expires == null || cookie.expires!!.timestamp > now)
        }.map { it.second }
    }

    override fun close() {
        cookieJar.clear()
        saveToDisk()
    }
}
