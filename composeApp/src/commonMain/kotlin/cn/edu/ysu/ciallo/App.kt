package cn.edu.ysu.ciallo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import cn.edu.ysu.ciallo.home.ClubRecommendPage
import org.jetbrains.compose.ui.tooling.preview.Preview
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cn.edu.ysu.ciallo.home.HomePageTab
import cn.edu.ysu.ciallo.home.LoginPage
import cafe.adriel.voyager.navigator.Navigator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

@Composable
fun MainScreen() {
    Navigator(HomePageTab()) { navigator ->
        if (navigator.lastItem is LoginPage) {
            navigator.lastItem.Content()
        } else {
            TabNavigator(HomePageTab()) { tabNavigator ->
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = tabNavigator.current == HomePageTab(),
                                onClick = { tabNavigator.current = HomePageTab() },
                                icon = { Icon(Icons.Default.Home, contentDescription = "主页") },
                                label = { Text("主页") }
                            )
                            NavigationBarItem(
                                selected = tabNavigator.current == ClubRecommendPage,
                                onClick = { tabNavigator.current = ClubRecommendPage },
                                icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "社团推荐") },
                                label = { Text("社团推荐") }
                            )
                        }
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        tabNavigator.current.Content()
                    }
                }
            }
        }
    }
}

@Composable
fun App() {
    MaterialTheme { MainScreen() }
}

@Composable
@Preview
fun PreviewApp() {
    MainScreen()
}
