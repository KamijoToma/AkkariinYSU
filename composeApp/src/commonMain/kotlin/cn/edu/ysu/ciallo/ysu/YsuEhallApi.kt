package cn.edu.ysu.ciallo.ysu

import androidx.compose.ui.text.LinkAnnotation
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.*
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.request
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 定义登录操作可能的结果。
 */
sealed class LoginResult {
    /** 登录成功 */
    object Success : LoginResult()

    /** 登录失败，并附带原因 */
    data class Failure(val reason: String) : LoginResult()
}

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Failure(val reason: String) : LoginUiState()
}

@kotlinx.serialization.Serializable
data class CardBalanceResponse(
    val id: String? = null,
    val datas: CardData? = null,
    val status: Int? = null,
    val cardstatuscode: String? = null,
    val cardnum: String? = null,
    val availdate: String? = null,
    val remining: String? = null, // 余额
    val code: Int? = null,
    val yearMonths: List<String>? = null,
    val cardstatusname: String? = null
)

@kotlinx.serialization.Serializable
data class CardData(
    @kotlinx.serialization.SerialName("DM") val dm: String? = null,
    @kotlinx.serialization.SerialName("KYXQ") val kyxq: String? = null,
    @kotlinx.serialization.SerialName("KH") val kh: String? = null,
    @kotlinx.serialization.SerialName("KNYE") val knye: Double? = null,
    @kotlinx.serialization.SerialName("MC") val mc: String? = null,
    @kotlinx.serialization.SerialName("SFRZH") val sfrzh: String? = null
)

/**
 * 提供与燕山大学网上服务大厅 (ehall) 交互的 API。
 *
 */
class YsuEhallApi {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState = _loginState.asStateFlow()

    fun resetLoginState() {
        _loginState.value = LoginUiState.Idle
    }

    companion object {
        private const val LOGIN_URL = "https://cer.ysu.edu.cn/authserver/login?service=https%3A%2F%2Fehall.ysu.edu.cn%2Flogin"
        private const val CHECK_CAPTCHA_URL = "https://cer.ysu.edu.cn/authserver/checkNeedCaptcha.htl"
        private const val CAPTCHA_URL = "https://cer.ysu.edu.cn/authserver/getCaptcha.htl"
        private const val BASE_URL = "https://ehall.ysu.edu.cn"
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
        followRedirects = false // 手动处理重定向
        defaultRequest {
            header(
                HttpHeaders.UserAgent,
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
            )
        }
    }

    private var lt: String? = null
    private var execution: String? = null
    private var salt: String? = null
    private var cllt: String = "userNameLogin"
    private var dllt: String = "generalLogin"
    private var _eventId: String = "submit"
    private var captcha: String? = null


    private suspend fun fetchLoginPage(): Boolean {
        return try {
            val response = client.get(LOGIN_URL)
            val soup = com.fleeksoft.ksoup.Ksoup.parse(response.bodyAsText())
            val form = soup.getElementById("pwdFromId")
            if (form == null) {
                println("错误：未能找到ID为 'pwdFromId' 的登录表单。")
                return false
            }
            lt = form.select("input[name=lt]").attr("value")
            execution = form.select("input[name=execution]").attr("value")
            salt = form.select("input[id=pwdEncryptSalt]").attr("value")
            cllt = form.select("input[name=cllt]").attr("value").ifEmpty { "userNameLogin" }
            dllt = form.select("input[name=dllt]").attr("value").ifEmpty { "generalLogin" }
            _eventId = form.select("input[name=_eventId]").attr("value").ifEmpty { "submit" }

            if (execution.isNullOrEmpty() || salt.isNullOrEmpty()) {
                println("错误：未能从登录页面获取到所有必要的参数。")
                return false
            }
            true
        } catch (e: Exception) {
            println("错误：访问或解析登录页面失败: ${e.message}")
            false
        }
    }

    suspend fun login(username: String, password: String) {
        _loginState.value = LoginUiState.Loading
        if (!fetchLoginPage()) {
            _loginState.value = LoginUiState.Failure("无法获取登录页面信息")
            return
        }

        // 检查是否需要验证码的逻辑可以根据需要添加
        // val needsCaptcha = client.post(CHECK_CAPTCHA_URL) { ... }

        val encPwd = PasswordEncryptor.encryptPassword(password, salt!!)
        val data = mapOf(
            "username" to username,
            "password" to encPwd,
            "captcha" to (captcha ?: ""),
            "lt" to lt!!,
            "execution" to execution!!,
            "_eventId" to _eventId,
            "cllt" to cllt,
            "dllt" to dllt,
        )

        try {
            var response = client.submitForm(
                url = LOGIN_URL,
                formParameters = Parameters.build {
                    data.forEach { (key, value) ->
                        append(key, value)
                    }
                },
            )

            var redirectCount = 0
            while (response.status in listOf(HttpStatusCode.Found, HttpStatusCode.SeeOther) && redirectCount < 10) {
                val location = response.headers[HttpHeaders.Location]
                if (location != null) {
                    println("Redirecting to: $location")
                    response = client.get(if (response.status == HttpStatusCode.Found) {
                        location
                    } else {
                        // location is /index.html, which is a relative URL
                        val original_url = response.request.url
                        original_url.protocol.name + "://" + original_url.host + location
                    })
                    redirectCount++
                } else {
                    _loginState.value = LoginUiState.Failure("登录重定向失败，未找到Location头")
                    return
                }
            }

            val finalUrl = response.request.url.toString()
            val responseText = response.bodyAsText()

            if (response.status.isSuccess() && finalUrl.startsWith("$BASE_URL/index.html") && "统一身份认证" !in responseText) {
                println("确认登录成功。")
                _loginState.value = LoginUiState.Success
            } else {
                val soup = com.fleeksoft.ksoup.Ksoup.parse(responseText)
                val errorMsg = soup.getElementById("showErrorTip")?.text() ?: "登录重定向后验证失败"
                println("登录失败：$errorMsg")
                _loginState.value = LoginUiState.Failure(errorMsg)
            }
        } catch (e: Exception) {
            println("登录请求失败: ${e.message}")
            _loginState.value = LoginUiState.Failure("登录请求异常: ${e.message}")
        }
    }

    suspend fun getLoginUser(): String? {
        return try {
            val response = client.get("$BASE_URL/getLoginUser")
            if (response.status.isSuccess()) {
                response.bodyAsText()
            } else {
                null
            }
        } catch (e: Exception) {
            println("获取用户信息失败: ${e.message}")
            null
        }
    }

    suspend fun getCardBalance(): CardBalanceResponse? {
        val url = "$BASE_URL/publicapp/sys/myyktzd/mySmartCard/loadSmartCardBillMain.do"
        return try {
            val response = client.get(url) {
                // Ktor 默认情况下会为重定向的请求添加原始请求的 Host 头，
                // 但目标服务器 (cer.ysu.edu.cn) 可能需要自己的 Host 头。
                // 明确设置 Host 头可以避免一些潜在的服务器配置问题。
                header(HttpHeaders.Host, Url(url).host)
            }
            if (response.status.isSuccess()) {
                response.body<CardBalanceResponse>()
            } else {
                println("获取校园卡余额失败: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("获取校园卡余额异常: ${e.message}")
            null
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return try {
            val response = client.get("$BASE_URL/getLoginUser")
            response.status.isSuccess() && response.bodyAsText().isNotEmpty()
        } catch (e: Exception) {
            println("检查登录状态失败: ${e.message}")
            false
        }
    }
}
