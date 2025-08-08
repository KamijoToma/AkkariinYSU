package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.components.CardBalanceCard
import cn.edu.ysu.ciallo.components.GpaCard
import cn.edu.ysu.ciallo.components.LibraryCard
import cn.edu.ysu.ciallo.components.NetworkCard
import cn.edu.ysu.ciallo.components.ScheduleCard
import cn.edu.ysu.ciallo.di.previewModule
import cn.edu.ysu.ciallo.gpa.GpaViewModel
import cn.edu.ysu.ciallo.studentinfo.StudentInfoPage
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.koinInject

object HomePageTab : Tab {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        HomePageContent(
            onNavigateToLogin = {
                navigator.parent?.push(LoginPage()) // 跳转到登录页面
            },
            onNavigateToProfile = {
                navigator.parent?.push(StudentInfoPage()) // 跳转到个人信息页面
            },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToProfile: () -> Unit,
    homeViewModel: HomeViewModel = koinInject(),
    cardBalanceViewModel: CardBalanceViewModel = koinInject(),
    gpaViewModel: GpaViewModel = koinInject()
) {
    // Load data when the composable is first launched, using cached data if available
    LaunchedEffect(Unit) {
        homeViewModel.loadData()
        cardBalanceViewModel.loadCardBalance()
        gpaViewModel.loadGpaInfo()
    }

    val homeData = homeViewModel.homeData.value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("主页") },
                actions = {
                    IconButton(onClick = {
                        homeViewModel.refreshData() // 刷新个人信息
                        cardBalanceViewModel.refreshCardBalance() // 刷新校园卡余额
                        gpaViewModel.loadGpaInfo() // 刷新绩点信息
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp), // 调整整体内边距
            verticalArrangement = Arrangement.spacedBy(16.dp) // 卡片之间的统一间距
        ) {
            Spacer(modifier = Modifier.height(8.dp)) // 顶部间距
            Text(
                "晚上好，${homeData.userName}",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
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
                    onClick = onNavigateToProfile,
                    label = { Text("已登录：${homeData.studentType}") },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Spacer(modifier = Modifier.height(8.dp)) // 欢迎语和卡片之间的间距

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
                onRefresh = { cardBalanceViewModel.refreshCardBalance() },
                onDetails = { /* TODO: 跳转到卡详情 */ },
            )
            GpaCard(
                gpaState = gpaViewModel.uiState.value,
                onRefresh = { gpaViewModel.loadGpaInfo() }
            )
            NetworkCard(
                networkInfo = "data.networkInfo"
            )
            Spacer(modifier = Modifier.height(8.dp)) // 底部间距
        }
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        HomePageContent(onNavigateToLogin = {}, onNavigateToProfile = {})
    }
}
