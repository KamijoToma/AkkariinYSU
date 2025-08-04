package cn.edu.ysu.ciallo.ysu

import cn.edu.ysu.ciallo.cardbalance.NetworkCookieManager
import com.fleeksoft.ksoup.Ksoup
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/**
 * 定义登录操作可能的结果。
 */
sealed class LoginResult {
    /** 登录成功 */
    object Success : LoginResult()

    /** 登录失败，并附带原因 */
    data class Failure(val reason: String) : LoginResult()

    /** 登录需要验证码，当前版本不支持 */
    object CaptchaRequired : LoginResult()
}

/**
 * 封装与燕山大学网上服务大厅 (E-Hall) 交互的API客户端。
 * @param cookieManager 用于管理和共享Cookie。
 */
class YsuEhallApi(private val cookieManager: NetworkCookieManager) {

    @Serializable
    private data class CaptchaResponse(val isNeed: Boolean = false)

    private val client = HttpClient {
        // 安装内容协商插件，用于自动序列化/反序列化JSON
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // 忽略JSON中未在数据类中定义的字段
                isLenient = true // 放宽对JSON格式的要求
            })
        }
        // 安装Cookie管理插件
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage() // 接受所有Cookie
        }
        // 配置默认请求行为
        followRedirects = false // E-Hall登录流程需要手动处理重定向
    }

    // API端点常量
    companion object {
        private const val BASE_URL = "https://ehall.ysu.edu.cn"
        private const val AUTH_BASE_URL = "https://cer.ysu.edu.cn/authserver"
        private const val LOGIN_URL = "$AUTH_BASE_URL/login?service=$BASE_URL/login"
        private const val CHECK_CAPTCHA_URL = "$AUTH_BASE_URL/checkNeedCaptcha.htl"
        private const val CARD_BALANCE_URL = "$BASE_URL/publicapp/sys/myyktzd/mySmartCard/loadSmartCardBillMain.do"
    }

    // 存储从登录页面获取的表单参数
    private var lt: String? = null
    private var execution: String? = null
    private var salt: String? = null
    private var _eventId: String? = null

    /**
     * 检查登录是否需要验证码。
     * @param username 用户名
     * @return 如果需要验证码，返回true。
     */
    private suspend fun needsCaptcha(username: String): Boolean {
        return try {
            val response: CaptchaResponse = client.post(CHECK_CAPTCHA_URL) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("username", username)
                }))
            }.body()
            response.isNeed
        } catch (e: Exception) {
            println("警告：检查验证码失败，默认不需要验证码。错误: ${e.message}")
            false
        }
    }


    /**
     * 异步登录到E-Hall。
     * @param username 用户名
     * @param password 密码
     * @return 返回一个 LoginResult 对象，表示登录的结果。
     */
    suspend fun login(username: String, password: String): LoginResult {
        if (!fetchLoginPage()) {
            return LoginResult.Failure("获取登录页面参数失败")
        }

        if (needsCaptcha(username)) {
            println("错误：需要验证码，当前版本不支持。")
            return LoginResult.CaptchaRequired
        }

        val encryptedPassword = PasswordEncryptor.encryptPassword(password, salt!!)

        try {
            val response = client.post(LOGIN_URL) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(FormDataContent(Parameters.build {
                    append("username", username)
                    append("password", encryptedPassword)
                    append("lt", lt!!)
                    append("execution", execution!!)
                    append("_eventId", _eventId!!)
                    append("cllt", "userNameLogin")
                    append("dllt", "generalLogin")
                }))
                // 模拟浏览器User-Agent
                header(HttpHeaders.UserAgent, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
            }

            // 登录成功后，服务器会返回302重定向
            if (response.status == HttpStatusCode.Found) {
                val location = response.headers[HttpHeaders.Location]
                println("登录成功，正在跳转到: $location")
                // 访问重定向地址以完成登录并获取会话Cookie
                client.get(location!!)
                // 将获取到的Cookie存储到共享的CookieManager中
                val cookies = client.cookies(BASE_URL)
                cookieManager.setCookies(cookies.associate { it.name to it.value })
                return if (cookieManager.isLoggedIn()) LoginResult.Success else LoginResult.Failure("登录后未能获取有效会话")
            } else {
                // 登录失败，解析错误信息
                val responseBody = response.bodyAsText()
                val doc = Ksoup.parse(responseBody)
                val errorMsg = doc.selectFirst("#showErrorTip")?.text() ?: "未知登录错误"
                println("登录失败: $errorMsg")
                return LoginResult.Failure(errorMsg)
            }
        } catch (e: Exception) {
            println("登录请求异常: ${e.message}")
            return LoginResult.Failure("网络请求异常")
        }
    }

    /**
     * 获取一卡通余额。
     * @return 返回包含余额信息的响应数据模型。
     */
    suspend fun getCardBalance(): YsuCardBalanceResponse {
        return client.get(CARD_BALANCE_URL) {
            // 将共享的Cookie附加到请求头
            cookieManager.getCookies().forEach { (name, value) ->
                cookie(name, value)
            }
        }.body()
    }

    /**
     * 获取登录页面，解析并存储必要的表单参数。
     * @return 如果成功解析到所有参数，返回true。
     */
    private suspend fun fetchLoginPage(): Boolean {
        try {
            val response: HttpResponse = client.get(LOGIN_URL)
            val body = response.bodyAsText()
            val doc = Ksoup.parse(body)
            val form = doc.selectFirst("#pwdFromId")
            if (form == null) {
                println("Error: Failed to find login form in the response.")
                return false
            }
            lt = form.selectFirst("input[name=lt]")?.attr("value").orEmpty()
            execution = form.selectFirst("input[name=execution]")?.attr("value")
            salt = form.selectFirst("#pwdEncryptSalt")?.attr("value")
            _eventId = form.selectFirst("input[name=_eventId]")?.attr("value")

            return lt != null && !execution.isNullOrBlank() && !salt.isNullOrBlank()
        } catch (e: Exception) {
            println("Error: Failed to access login page: ${e.message}")
            return false
        }
    }

    fun isLoggedIn(): Boolean {
        return cookieManager.isLoggedIn()
    }
}
