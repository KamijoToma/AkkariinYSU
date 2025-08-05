package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository
import cn.edu.ysu.ciallo.components.CardBalanceCard
import cn.edu.ysu.ciallo.components.LibraryCard
import cn.edu.ysu.ciallo.components.NetworkCard
import cn.edu.ysu.ciallo.components.ScheduleCard
import org.jetbrains.compose.ui.tooling.preview.Preview

class HomePageTab(
    private val useRemoteApi: Boolean = true
) : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        HomePageContent(
            useRemoteApi = useRemoteApi,
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
    useRemoteApi: Boolean = true,
    onNavigateToLogin: () -> Unit
) {
    val homeViewModel: HomeViewModel = remember(useRemoteApi) {
        if (useRemoteApi) {
            HomeViewModel(RemoteHomeRepository())
        } else {
            HomeViewModel(FakeHomeRepository())
        }
    }

    // refresh home data
    homeViewModel.loadData()
    val homeData = homeViewModel.homeData.value


    val cardBalanceViewModel = remember(useRemoteApi) {
        val repository = if (useRemoteApi) {
            RemoteCardBalanceRepository()
        } else {
            MockCardBalanceRepository()
        }
        CardBalanceViewModel(repository)
    }


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
            LaunchedEffect(homeData){
                cardBalanceViewModel.loadCardBalance()
            }
            NetworkCard(
                networkInfo = "data.networkInfo"
            )
        }
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    // 预览时用Mock数据
    HomePageContent(useRemoteApi = false, onNavigateToLogin = {})
}
