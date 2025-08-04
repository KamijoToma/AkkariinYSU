package cn.edu.ysu.ciallo.ysu

import cn.edu.ysu.ciallo.cardbalance.NetworkCookieManager

/**
 * YsuEhallApi 的工厂类，用于创建和管理API客户端实例。
 * 采用单例模式确保应用内共享同一个登录会话。
 */
object YsuEhallApiFactory {
    // 用户凭据，实际应用中应从安全存储中获取
    private var username = ""
    private var password = ""

    // 单例的 NetworkCookieManager，用于在API层和Repository层之间共享Cookie
    private val cookieManager = NetworkCookieManager()

    // YsuEhallApi 的懒加载单例实例
    private val apiInstance: YsuEhallApi by lazy {
        YsuEhallApi()
    }

    /**
     * 获取 YsuEhallApi 的单例实例。
     */
    fun getInstance(): YsuEhallApi {
        return apiInstance
    }

    /**
     * 设置用户凭据。
     * @param user 用户名
     * @param pass 密码
     */
    fun setCredentials(user: String, pass: String) {
        username = user
        password = pass
    }

    /**
     * 获取当前设置的用户名。
     */
    fun getUsername(): String = username

    /**
     * 获取当前设置的密码。
     */
    fun getPassword(): String = password
}

