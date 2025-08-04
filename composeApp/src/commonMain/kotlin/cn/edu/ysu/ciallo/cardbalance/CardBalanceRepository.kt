package cn.edu.ysu.ciallo.cardbalance

import cn.edu.ysu.ciallo.cardbalance.CardBalanceResult

interface CardBalanceRepository {
    suspend fun getCardBalance(): CardBalanceResult
}

class MockCardBalanceRepository : CardBalanceRepository {
    override suspend fun getCardBalance(): CardBalanceResult {
        // 可根据需要模拟不同情况
        return CardBalanceResult.Success(
            // Returning a mock balance data with current time as last update
            CardBalanceData(balance = 123.45, lastUpdate = System.currentTimeMillis())
        )
        // return CardBalanceResult.Failure(CardBalanceError.NotLoggedIn)
    }
}

class RemoteCardBalanceRepository(
    private val cookieManager: NetworkCookieManager
) : CardBalanceRepository {
    override suspend fun getCardBalance(): CardBalanceResult {
        // 伪代码，实际应发起网络请求并处理cookie
        try {
            if (!cookieManager.isLoggedIn()) {
                return CardBalanceResult.Failure(CardBalanceError.NotLoggedIn)
            }
            // val response = ... // 发起网络请求，带cookie
            // if (response.success) ...
            // else ...
            return CardBalanceResult.Failure(CardBalanceError.UnknownError)
        } catch (e: Exception) {
            return CardBalanceResult.Failure(CardBalanceError.NetworkError)
        }
    }
}

