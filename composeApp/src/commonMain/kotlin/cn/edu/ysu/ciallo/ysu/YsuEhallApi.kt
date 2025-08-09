package cn.edu.ysu.ciallo.ysu

import cn.edu.ysu.ciallo.gpa.GpaResponse
import cn.edu.ysu.ciallo.library.LibrarySeatOverviewResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


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
    private val cookieStorage = CustomCookieStorage()
    val loginState = _loginState.asStateFlow()

    companion object {
        private const val LOGIN_URL =
            "https://cer.ysu.edu.cn/authserver/login?service=https%3A%2F%2Fehall.ysu.edu.cn%2Flogin"
        private const val CHECK_CAPTCHA_URL = "https://cer.ysu.edu.cn/authserver/checkNeedCaptcha.htl"
        private const val CAPTCHA_URL = "https://cer.ysu.edu.cn/authserver/getCaptcha.htl"
        private const val BASE_URL = "https://ehall.ysu.edu.cn"
        private const val STUDENT_BASE_INFO_URL = "$BASE_URL/xsfw/sys/jbxxapp/modules/infoStudent/getStuBaseInfo.do"
        private const val INDEX_DO_URL = "$BASE_URL/xsfw/sys/jbxxapp/*default/index.do"
        private const val APP_ID_JBXX = "4585275700341858"
        private const val APP_NAME_JBXX = "jbxxapp"

        // 教务系统相关URL
        private const val JWXT_BASE_URL = "https://jwxt.ysu.edu.cn"
        private const val GPA_QUERY_URL = "$JWXT_BASE_URL/jwapp/sys/cjcx/modules/cjcx/cxzxfaxfjd.do"
        private const val GPA_REFERER_URL = "$JWXT_BASE_URL/jwapp/sys/cjcx/*default/index.do?EMAP_LANG=zh"

        // 绩点查询相关App配置
        private const val APP_NAME_GPA = "cjcx" // 成绩查询应用名称
        private const val APP_ID_GPA = "4768574631264620" // 成绩查询应用ID，可能需要动态获取
        private const val GPA_GET_APP_CONFIG_URL = "$JWXT_BASE_URL/jwapp/sys/funauthapp/api/getAppConfig/$APP_NAME_GPA-$APP_ID_GPA.do"

        // 图书馆系统相关URL
        private const val LIBRARY_BASE_URL = "http://seat.ysu.edu.cn"
        private const val LIBRARY_SEAT_OVERVIEW_URL = "$LIBRARY_BASE_URL/ic-web/seatMenu"
    }

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(HttpCookies) {
            storage = cookieStorage
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.ALL
        }
        followRedirects = true
        install(HttpRedirect) {
            // 允许重定向
            allowHttpsDowngrade = true
            checkHttpMethod = true
        }
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

    private suspend fun getCurrentUserStudentId(): String? {
        return try {
            val response = client.get(INDEX_DO_URL)
            val soup = com.fleeksoft.ksoup.Ksoup.parse(response.bodyAsText())

            val scriptTag = soup.select("script").firstOrNull { it.html().contains("var pageMeta = {") }

            if (scriptTag != null) {
                val scriptContent = scriptTag.html()
                val match = Regex("var pageMeta = (\\{.*?\\});").find(scriptContent)
                if (match != null) {
                    val pageMetaJsonStr = match.groupValues[1]
                    val pageMeta = kotlinx.serialization.json.Json.parseToJsonElement(pageMetaJsonStr).jsonObject
                    val studentId = pageMeta["params"]?.jsonObject?.get("USERID")?.jsonPrimitive?.content
                    if (studentId != null) {
                        println("成功从页面获取当前用户学号: $studentId")
                        return studentId
                    } else {
                        println("在 pageMeta 中未找到 USERID。")
                    }
                } else {
                    println("未能从 script 标签内容中匹配到 pageMeta JSON。")
                }
            } else {
                println("未找到包含 'var pageMeta =' 的 script 标签。")
            }
            null
        } catch (e: Exception) {
            println("访问 $INDEX_DO_URL 页面失败或解析内容失败: ${e.message}")
            null
        }
    }

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

    suspend fun login(username: String, password: String, rememberMe: Boolean = false): LoginResult {
        _loginState.value = LoginUiState.Loading
        if (!fetchLoginPage()) {
            _loginState.value = LoginUiState.Failure("无法获取登录页面信息")
            return LoginResult.Failure("无法获取登录页面信息")
        }

        // 检查是否需要验证码的逻辑可以根据需要添加
        // val needsCaptcha = client.post(CHECK_CAPTCHA_URL) { ... }

        val encPwd = PasswordEncryptor.encryptPassword(password, salt!!)
        val data = mutableMapOf(
            "username" to username,
            "password" to encPwd,
            "captcha" to (captcha ?: ""),
            "lt" to lt!!,
            "execution" to execution!!,
            "_eventId" to _eventId,
            "cllt" to cllt,
            "dllt" to dllt,
        )

        if (rememberMe) {
            data["rememberMe"] = "true"
            println("已启用7天免登录功能")
        }

        try {
            val response = client.submitForm(
                url = LOGIN_URL,
                formParameters = Parameters.build {
                    data.forEach { (key, value) ->
                        append(key, value)
                    }
                },
            )

            val responseText = response.bodyAsText()

            if (response.status == HttpStatusCode.Found) {
                println("确认登录成功。")
                // save cookies to storage
                cookieStorage.saveToDisk()
                _loginState.value = LoginUiState.Success
                client.get(response.headers[HttpHeaders.Location]!!)
                return LoginResult.Success
            } else {
                val soup = com.fleeksoft.ksoup.Ksoup.parse(responseText)
                val errorMsg = soup.getElementById("showErrorTip")?.text() ?: "登录重定向后验证失败"
                println("登录失败：$errorMsg")
                _loginState.value = LoginUiState.Failure(errorMsg)
                return LoginResult.Failure(errorMsg)
            }
        } catch (e: Exception) {
            println("登录请求失败: ${e.message}")
            _loginState.value = LoginUiState.Failure("登录请求异常: ${e.message}")
            return LoginResult.Failure("登录请求异常: ${e.message}")
        }
    }

    suspend fun getLoginUser(): StudentBaseInfo? {
        return getStudentBaseInfo()
    }

    suspend fun getCardBalance(): CardBalanceResponse? {
        val url = "$BASE_URL/publicapp/sys/myyktzd/mySmartCard/loadSmartCardBillMain.do"
        return try {
            val response = client.get(url)
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
            getLoginUser() != null
        } catch (e: Exception) {
            println("检查登录状态失败: ${e.message}")
            false
        }
    }

    private suspend fun _preGpaQuery(): Boolean {
        return try {
            client.get(GPA_REFERER_URL)
            true
        } catch (e: Exception) {
            println("访问绩点查询 Referer URL 失败: ${e.message}")
            false
        }
    }

    suspend fun getGpaInfo(studentId: String? = null): GpaResponse? {
        val actualStudentId = studentId ?: getCurrentUserStudentId()
        if (actualStudentId == null) {
            println("未能获取当前用户的学号，无法查询绩点信息。")
            return null
        }

        // 1. 访问绩点查询应用配置页面 (根据用户反馈，不独立为函数，不考虑返回值)
        try {
            println("正在访问绩点查询应用配置页面: $GPA_GET_APP_CONFIG_URL")
            client.get(GPA_GET_APP_CONFIG_URL)
        } catch (e: Exception) {
            println("访问绩点查询应用配置页面失败: ${e.message}")
            // 即使失败也继续，因为用户要求不考虑返回值
        }

        // 2. 访问 Referer URL 以确保设置必要的 cookies 和 Referer 头
        if (!_preGpaQuery()) {
            return null
        }

        val data = mapOf(
            "XH1" to actualStudentId,
            "XH2" to actualStudentId,
            "XH3" to actualStudentId,
            "XH4" to actualStudentId,
            "XH5" to actualStudentId,
            "XH6" to actualStudentId
        )

        println("正在查询学号 $actualStudentId 的学分绩点信息...")
        return try {
            val response = client.post(GPA_QUERY_URL) {
                setBody(FormDataContent(Parameters.build {
                    data.forEach { (key, value) ->
                        append(key, value)
                    }
                }))
                contentType(ContentType.Application.FormUrlEncoded)
            }
            if (response.status.isSuccess()) {
                response.body<GpaResponse>()
            } else {
                println("获取学分绩点信息失败: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("获取学分绩点信息异常: ${e.message}")
            null
        }
    }

    suspend fun getStudentBaseInfo(studentId: String? = null): StudentBaseInfo? {
        val actualStudentId = studentId ?: getCurrentUserStudentId()
        if (actualStudentId == null) {
            println("未能获取当前用户的学号，无法查询学生基本信息。")
            return null
        }

        // 需要先访问一次获取 appConfig cookies
        try {
            client.post("$BASE_URL/xsfw/sys/swpubapp/indexmenu/getAppConfig.do") {
                url {
                    parameters.append("appId", APP_ID_JBXX)
                    parameters.append("appName", APP_NAME_JBXX)
                }
            }
        } catch (e: Exception) {
            println("获取学生基本信息前获取 appConfig 失败: ${e.message}")
            return null
        }

        return try {
            val response = client.post(STUDENT_BASE_INFO_URL) {
                setBody(FormDataContent(Parameters.build {
                    append("requestParamStr", "{\"XSBH\":\"$actualStudentId\"}")
                }))
                contentType(ContentType.Application.FormUrlEncoded)
            }
            if (response.status.isSuccess()) {
                response.body<StudentBaseInfoResponse>().data
            } else {
                println("获取学生基本信息失败: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("获取学生基本信息异常: ${e.message}")
            null
        }
    }

    suspend fun getLibrarySeatOverview(): LibrarySeatOverviewResponse? {
        return try {
            val response = client.get(LIBRARY_SEAT_OVERVIEW_URL) {
                header("Accept", "application/json, text/plain, */*")
                header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6,zh-TW;q=0.5")
                header("Cache-Control", "no-cache")
                header("DNT", "1")
                header("Pragma", "no-cache")
                header("Proxy-Connection", "keep-alive")
                header("lan", "1")
            }
            if (response.status.isSuccess()) {
                response.body<LibrarySeatOverviewResponse>()
            } else {
                println("获取图书馆座位概览失败: ${response.status}")
                null
            }
        } catch (e: Exception) {
            println("获取图书馆座位概览异常: ${e.message}")
            null
        }
    }

    @kotlinx.serialization.Serializable
    data class StudentBaseInfoResponse(
        val returnCode: String,
        val description: String,
        val data: StudentBaseInfo? = null
    )
}
