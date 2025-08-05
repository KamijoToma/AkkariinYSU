package cn.edu.ysu.ciallo

import androidx.compose.runtime.Composable
import androidx.compose.material3.MaterialTheme
import cafe.adriel.voyager.navigator.Navigator
import cn.edu.ysu.ciallo.di.appModule
import cn.edu.ysu.ciallo.di.previewModule
import cn.edu.ysu.ciallo.home.MainScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.core.context.startKoin

@Composable
fun App() {
    startKoin { modules(appModule) }
    MaterialTheme {
        Navigator(MainScreen())
    }
}

@Preview
@Composable
fun PreviewApp() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        App()
    }
}
