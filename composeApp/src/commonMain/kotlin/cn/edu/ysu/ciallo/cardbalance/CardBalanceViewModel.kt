package cn.edu.ysu.ciallo.cardbalance

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.State
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CardBalanceViewModel(
    private val repository: CardBalanceRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private val _uiState = mutableStateOf<CardBalanceResult?>(null)
    val uiState: State<CardBalanceResult?> get() = _uiState

    fun loadCardBalance() {
        coroutineScope.launch {
            val result = repository.getCardBalance()
            _uiState.value = result
        }
    }
}

