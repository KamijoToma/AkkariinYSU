package cn.edu.ysu.ciallo.cardbalance

import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory

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

/**
 * CardBalanceRepository 的远程实现，通过 YsuEhallApi 从服务器获取数据。
 * @param api YSU E-Hall API的实例，默认为通过工厂获取的单例。
 */
class RemoteCardBalanceRepository(
    private val api: YsuEhallApi = YsuEhallApiFactory.getInstance()
) : CardBalanceRepository {
    override suspend fun getCardBalance(): CardBalanceResult {
        return try {
            // 检查是否已登录
            if (!api.isLoggedIn()) {
                return CardBalanceResult.Failure(CardBalanceError.NotLoggedIn)
            }

            // 获取一卡通余额
            val response = api.getCardBalance()

            // 检查API响应是否成功
            if (response != null && response.code == 200 && response.remining != null) {
                println("成功获取卡余额: ${response.remining}")
                CardBalanceResult.Success(
                    CardBalanceData(
                        balance = response.remining.toDoubleOrNull() ?: 0.0,
                        lastUpdate = System.currentTimeMillis()
                    )
                )
            } else {
                println("获取卡余额失败，结果: $response")
                // API返回了错误码或无效数据
                CardBalanceResult.Failure(CardBalanceError.UnknownError)
            }
        } catch (e: Exception) {
            // 捕获网络请求或其他异常
            e.printStackTrace()
            CardBalanceResult.Failure(CardBalanceError.NetworkError)
        }
    }
}
