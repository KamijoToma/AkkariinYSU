package cn.edu.ysu.ciallo.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) {
    var homeData: MutableState<HomeData> = mutableStateOf(DEFAULT_HOME_DATA)
        private set

    fun loadData() {
        CoroutineScope(Dispatchers.Default).launch {
            homeData.value = repository.getHomeData()
        }
    }
}


