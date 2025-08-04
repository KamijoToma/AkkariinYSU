package cn.edu.ysu.ciallo.cardbalance

/**
 * 简单的 Cookie 管理器，支持多API共用登录态。
 * 实际项目可根据平台实现持久化与同步。
 */
class NetworkCookieManager {
    private var cookies: Map<String, String> = emptyMap()
    private var loggedIn: Boolean = false

    fun setCookies(newCookies: Map<String, String>) {
        cookies = newCookies
        loggedIn = cookies.isNotEmpty()
    }

    fun getCookies(): Map<String, String> = cookies

    fun isLoggedIn(): Boolean = loggedIn

    fun clear() {
        cookies = emptyMap()
        loggedIn = false
    }
}

