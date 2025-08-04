package cn.edu.ysu.ciallo.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) {
    var homeData: MutableState<HomeData?> = mutableStateOf(null)
        private set

    fun loadData() {
        CoroutineScope(Dispatchers.Default).launch {
            homeData.value = repository.getHomeData()
        }
    }
}

@Composable
fun rememberHomeViewModel(repository: HomeRepository = FakeHomeRepository()): HomeViewModel {
    val viewModel = remember { HomeViewModel(repository) }
    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    return viewModel
}

