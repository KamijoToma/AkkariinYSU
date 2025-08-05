package cn.edu.ysu.ciallo.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.edu.ysu.ciallo.ysu.LoginUiState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {
    val loginState: StateFlow<LoginUiState> = repository.loginState

    fun login(username: String, password: String) {
        viewModelScope.launch {
            repository.login(username, password)
        }
    }
}
