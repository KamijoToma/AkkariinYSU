package cn.edu.ysu.ciallo.home

import cn.edu.ysu.ciallo.ysu.LoginResult
import cn.edu.ysu.ciallo.ysu.LoginUiState
import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface LoginRepository {
    val loginState: StateFlow<LoginUiState>
    suspend fun login(username: String, password: String): LoginResult
}

class RemoteLoginRepository(private val api: YsuEhallApi) : LoginRepository {
    override val loginState: StateFlow<LoginUiState>
        get() = api.loginState

    override suspend fun login(username: String, password: String): LoginResult {
        return api.login(username, password)
    }
}

class FakeLoginRepository : LoginRepository {
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    override val loginState: StateFlow<LoginUiState> = _loginState

    override suspend fun login(username: String, password: String): LoginResult {
        _loginState.value = LoginUiState.Loading
        delay(1000) // Simulate network delay
        return if (username == "preview" && password == "preview") {
            _loginState.value = LoginUiState.Success
            LoginResult.Success
        } else {
            val reason = "用户名或密码错误"
            _loginState.value = LoginUiState.Failure(reason)
            LoginResult.Failure(reason)
        }
    }
}
