package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.components.CardBalanceCard
import cn.edu.ysu.ciallo.components.LibraryCard
import cn.edu.ysu.ciallo.components.NetworkCard
import cn.edu.ysu.ciallo.components.ScheduleCard
import cn.edu.ysu.ciallo.di.previewModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.koinInject

class HomePageTab : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        HomePageContent(
            onNavigateToLogin = {
                navigator.parent?.push(LoginPage()) // 跳转到登录页面
            }
        )
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            title = "主页",
            icon = null,
            index = 0u
        )
}

@Composable
fun HomePageContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    homeViewModel: HomeViewModel = koinInject(),
    cardBalanceViewModel: CardBalanceViewModel = koinInject()
) {
    // Load data when the composable is first launched
    LaunchedEffect(Unit) {
        homeViewModel.loadData()
        cardBalanceViewModel.loadCardBalance()
    }

    val homeData = homeViewModel.homeData.value


    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            Text(
                "晚上好，${homeData.userName}",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            if (!homeData.logged) {
                AssistChip(
                    onClick = onNavigateToLogin,
                    label = { Text("登录") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                AssistChip(
                    onClick = {},
                    label = { Text("已登录：${homeData.studentType}") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            ScheduleCard(
                today = "data.scheduleToday",
                tomorrow = "data.scheduleTomorrow",
                onMore = { /* TODO: 跳转日程 */ }
            )
            LibraryCard(
                electricityInfo = "data.electricityInfo",
                bookInfo = "data.bookInfo",
                libraryStatus = emptyList(),
            )
            CardBalanceCard(
                cardBalanceState = cardBalanceViewModel.uiState.value,
                onRefresh = { cardBalanceViewModel.loadCardBalance() },
                onDetails = { /* TODO: 跳转到卡详情 */ },
            )
            NetworkCard(
                networkInfo = "data.networkInfo"
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        HomePageContent(onNavigateToLogin = {})
    }
}
