package cn.edu.ysu.ciallo.cardbalance

/**
 * 定义卡余额功能可能遇到的所有特定错误类型。
 */
sealed class CardBalanceError : Exception() {
    /** 用户未登录或凭据未提供 */
    object NotLoggedIn : CardBalanceError()

    /** 网络请求失败 */
    object NetworkError : CardBalanceError()

    /** 登录失败，例如用户名或密码错误 */
    data class LoginFailed(val reason: String) : CardBalanceError()

    /** 登录需要验证码，当前版本不支持 */
    object CaptchaRequired : CardBalanceError()

    /** 未知错误 */
    object UnknownError : CardBalanceError()

    /** 自定义错误信息 */
    data class Custom(override val message: String) : CardBalanceError()
}

data class CardBalanceData(
    val balance: Double,
    val lastUpdate: Long
)

sealed class CardBalanceResult {
    data class Success(val data: CardBalanceData) : CardBalanceResult()
    data class Failure(val error: CardBalanceError) : CardBalanceResult()
}
