package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cn.edu.ysu.ciallo.ysu.LoginResult
import cn.edu.ysu.ciallo.ysu.LoginUiState
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory
import kotlinx.coroutines.launch

@Composable
fun LoginPage(onLoginSuccess: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val api = YsuEhallApiFactory.getInstance()
    val loginState by api.loginState.collectAsState()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("用户名") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("密码") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    api.login(username, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("登录")
        }
        Spacer(modifier = Modifier.height(8.dp))
        when (loginState) {

            is LoginUiState.Failure -> TODO()
            LoginUiState.Idle -> TODO()
            LoginUiState.Loading -> TODO()
            LoginUiState.Success -> TODO()
        }
    }
}
