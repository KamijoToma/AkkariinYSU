package cn.edu.ysu.ciallo.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibrarySeatViewModel(
    private val repository: LibrarySeatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LibrarySeatResult?>(null)
    val uiState: StateFlow<LibrarySeatResult?> get() = _uiState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    fun loadLibrarySeatOverview() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLibrarySeatOverview()
                _uiState.value = result
            } catch (e: Exception) {
                _uiState.value = LibrarySeatResult.Failure(LibrarySeatError.UnknownError)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshLibrarySeatOverview() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getLibrarySeatOverview()
                _uiState.value = result
            } catch (e: Exception) {
                _uiState.value = LibrarySeatResult.Failure(LibrarySeatError.UnknownError)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
