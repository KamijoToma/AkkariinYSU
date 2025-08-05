package cn.edu.ysu.ciallo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cn.edu.ysu.ciallo.di.appModule
import cn.edu.ysu.ciallo.di.previewModule
import cn.edu.ysu.ciallo.home.ClubRecommendPage
import cn.edu.ysu.ciallo.home.HomePageTab
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.core.context.startKoin

@Composable
fun App() {
    startKoin { modules(appModule) }
        MaterialTheme {
            TabNavigator(HomePageTab()) { tabNavigator ->
                Scaffold(
                    content = {
                        Box(modifier = Modifier.padding(it)) {
                            CurrentTab()
                        }
                    },
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
                )
            }
        }
}

@Preview
@Composable
fun PreviewApp() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        App()
    }
}
