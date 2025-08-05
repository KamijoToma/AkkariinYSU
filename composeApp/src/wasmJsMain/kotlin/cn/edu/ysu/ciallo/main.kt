package cn.edu.ysu.ciallo

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import cn.edu.ysu.ciallo.di.initKoin
import cn.edu.ysu.ciallo.ysu.initCookiePersistPath
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    initCookiePersistPath(null)
    ComposeViewport(document.body!!) {
        App()
    }
}
