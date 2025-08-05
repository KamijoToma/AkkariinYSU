package cn.edu.ysu.ciallo.cardbalance

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class CardBalanceViewModel(
    private val repository: CardBalanceRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _uiState = mutableStateOf<CardBalanceResult?>(null)
    val uiState: State<CardBalanceResult?> get() = _uiState

    private var cachedCardBalance: CardBalanceResult? = null
    private var lastFetchTime: Instant? = null
    private val cacheDuration = 5.minutes // 缓存有效期5分钟

    fun loadCardBalance() {
        coroutineScope.launch {
            val currentTime = Clock.System.now()
            if (cachedCardBalance != null && lastFetchTime != null && (currentTime - lastFetchTime!!) < cacheDuration) {
                // 缓存未过期，使用缓存数据
                _uiState.value = cachedCardBalance!!
            } else {
                // 缓存过期或不存在，从API获取新数据
                fetchAndCacheCardBalance()
            }
        }
    }

    fun refreshCardBalance() {
        coroutineScope.launch {
            fetchAndCacheCardBalance()
        }
    }

    private suspend fun fetchAndCacheCardBalance() {
        val result = repository.getCardBalance()
        cachedCardBalance = result
        lastFetchTime = Clock.System.now()
        _uiState.value = result
    }
}
