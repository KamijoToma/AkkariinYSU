package cn.edu.ysu.ciallo.ysu

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.slf4j.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertTrue

class YsuEhallApiTest {
    val api = runBlocking {
        val api = YsuEhallApi()
        assert(api.login(YsuEhallApiCredentials.USERNAME, YsuEhallApiCredentials.PASSWORD) == LoginResult.Success)
        api
    }

    @Test
    fun testGetUserInfo() = runTest {
        LoggerFactory.getLogger("YsuEhallApi")
        val balance = api.getCardBalance()
        assertTrue(balance != null && balance.code == 200, "Expected response code 200, got $balance")
        println(balance)
    }
}

