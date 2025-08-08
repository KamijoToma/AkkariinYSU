package cn.edu.ysu.ciallo.gpa

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GpaViewModel(
    private val gpaRepository: GpaRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<GpaUiState>(GpaUiState.Idle)
    val uiState: State<GpaUiState> get() = _uiState

    fun loadGpaInfo() {
        viewModelScope.launch {
            _uiState.value = gpaRepository.getGpaInfo()
        }
    }
}
