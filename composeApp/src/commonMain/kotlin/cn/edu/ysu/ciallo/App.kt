package cn.edu.ysu.ciallo

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cn.edu.ysu.ciallo.home.ClubRecommendPage
import cn.edu.ysu.ciallo.home.HomePage
import cn.edu.ysu.ciallo.home.LoginPage
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun MainScreen(
    useRemoteApi: Boolean = true,
) {
    var selectedTab by remember { mutableStateOf(0) }
    var showLoginPage by remember { mutableStateOf(false) }

    if (showLoginPage) {
        LoginPage(onLoginSuccess = { showLoginPage = false })
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "主页") },
                        label = { Text("主页") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "社团推荐") },
                        label = { Text("社团推荐") }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedTab) {
                0 -> HomePage(
                    Modifier.padding(innerPadding),
                    onNavigateToLogin = { showLoginPage = true },
                    useRemoteApi = useRemoteApi
                )
                1 -> ClubRecommendPage(Modifier.padding(innerPadding))
            }
        }
    }
}

@Composable
fun App() {
    MainScreen()
}

@Composable
@Preview
fun PreviewApp() {
    MainScreen(useRemoteApi = false)
}
