package cn.edu.ysu.ciallo.ysu

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * 自定义Cookie存储，支持线程安全和过期清理。
 */
class CustomCookieStorage : CookiesStorage {
    private val mutex = Mutex()
    private val cookieJar = mutableListOf<Pair<Url, Cookie>>()

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            if (cookie.name.equals("CASTGC", ignoreCase = true)) {
                // if CASTGC cookie is present in cookieJar and its value is not empty, return
                if (cookie.value.isBlank() && cookieJar.any { (url, c) -> /*url.host == requestUrl.host &&*/ c.name.equals("CASTGC", ignoreCase = true) && c.value.isNotEmpty() }) {
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
    }

    // 可选：清理过期cookie
    suspend fun cleanExpired() {
        val now = System.currentTimeMillis() / 1000
        mutex.withLock {
            cookieJar.removeAll { (_, cookie) -> cookie.expires != null && cookie.expires!!.timestamp <= now }
        }
    }
}

