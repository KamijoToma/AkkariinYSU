package cn.edu.ysu.ciallo.ysu

import cn.edu.ysu.ciallo.cardbalance.NetworkCookieManager
import kotlin.test.*
import kotlinx.coroutines.test.runTest

class YsuEhallApiTest {
    @Test
    fun testLoginWithInvalidCredentials() = runTest {
        val api = YsuEhallApi(
            cookieManager = NetworkCookieManager()
        )
        val result = api.login("wrongUser", "wrongPass")
        assertTrue(result is LoginResult.Failure || result is LoginResult.CaptchaRequired)
    }

    @Test
    fun testLoginWithValidCredentials() = runTest {
        val api = YsuEhallApi(
            cookieManager = NetworkCookieManager()
        )
        val result = api.login("xxxxxxxx", "xxxxxxxxxx")
        assertTrue(result is LoginResult.Success)
    }
}

