package cn.edu.ysu.ciallo.cardbalance

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
    private val api: cn.edu.ysu.ciallo.ysu.YsuEhallApi = cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory.getInstance()
) : CardBalanceRepository {
    override suspend fun getCardBalance(): CardBalanceResult {
        return try {
            // 检查是否已登录
            if (!api.isLoggedIn()) {
                println("用户未登录，尝试自动登录...")
                val username = cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory.getUsername()
                val password = cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory.getPassword()

                // 如果没有预设的用户名或密码，则直接返回未登录错误
                if (username.isBlank() || password.isBlank()) {
                    return CardBalanceResult.Failure(CardBalanceError.NotLoggedIn)
                }

                // 执行登录并处理结果
                when (val loginResult = api.login(username, password)) {
                    is cn.edu.ysu.ciallo.ysu.LoginResult.Success -> {
                        println("自动登录成功。")
                        // 登录成功，继续获取余额
                    }
                    is cn.edu.ysu.ciallo.ysu.LoginResult.Failure -> {
                        // 登录失败，返回具体的失败原因
                        return CardBalanceResult.Failure(CardBalanceError.LoginFailed(loginResult.reason))
                    }
                    is cn.edu.ysu.ciallo.ysu.LoginResult.CaptchaRequired -> {
                        // 需要验证码，返回特定错误
                        return CardBalanceResult.Failure(CardBalanceError.CaptchaRequired)
                    }
                }
            }

            // 获取一卡通余额
            println("正在获取卡余额...")
            val response = api.getCardBalance()

            // 检查API响应是否成功
            if (response.code == 200 && response.remining != null) {
                println("成功获取卡余额: ${response.remining}")
                CardBalanceResult.Success(
                    CardBalanceData(
                        balance = response.remining.toDoubleOrNull() ?: 0.0,
                        lastUpdate = System.currentTimeMillis()
                    )
                )
            } else {
                println("获取卡余额失败，响应代码: ${response.code}")
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
