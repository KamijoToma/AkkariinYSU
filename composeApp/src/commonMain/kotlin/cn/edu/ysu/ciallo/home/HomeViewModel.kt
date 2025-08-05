package cn.edu.ysu.ciallo.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.minutes

class HomeViewModel(private val repository: HomeRepository) {
    var homeData: MutableState<HomeData> = mutableStateOf(DEFAULT_HOME_DATA)
        private set

    private var cachedHomeData: HomeData? = null
    private var lastFetchTime: Instant? = null
    private val cacheDuration = 5.minutes // 缓存有效期5分钟

    fun loadData() {
        CoroutineScope(Dispatchers.Default).launch {
            val currentTime = Clock.System.now()
            if (cachedHomeData != null && lastFetchTime != null && (currentTime - lastFetchTime!!) < cacheDuration) {
                // 缓存未过期，使用缓存数据
                homeData.value = cachedHomeData!!
            } else {
                // 缓存过期或不存在，从API获取新数据
                fetchAndCacheData()
            }
        }
    }

    fun refreshData() {
        CoroutineScope(Dispatchers.Default).launch {
            fetchAndCacheData()
        }
    }

    private suspend fun fetchAndCacheData() {
        val newData = repository.getHomeData()
        cachedHomeData = newData
        lastFetchTime = Clock.System.now()
        homeData.value = newData
    }
}
