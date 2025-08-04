package cn.edu.ysu.ciallo.cardbalance

sealed class CardBalanceError : Exception() {
    object NotLoggedIn : CardBalanceError() {
        private fun readResolve(): Any = NotLoggedIn
    }

    object NetworkError : CardBalanceError() {
        private fun readResolve(): Any = NetworkError
    }

    object UnknownError : CardBalanceError() {
        private fun readResolve(): Any = UnknownError
    }

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

