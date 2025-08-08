package cn.edu.ysu.ciallo.gpa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GpaViewModel(
    private val gpaRepository: GpaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GpaUiState>(GpaUiState.Idle)
    val uiState: StateFlow<GpaUiState> = _uiState.asStateFlow()

    fun loadGpaInfo() {
        viewModelScope.launch {
            _uiState.value = GpaUiState.Loading
            _uiState.value = gpaRepository.getGpaInfo()
        }
    }
}
