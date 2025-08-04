package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository
import cn.edu.ysu.ciallo.components.CardBalanceCard
import cn.edu.ysu.ciallo.components.LibraryCard
import cn.edu.ysu.ciallo.components.NetworkCard
import cn.edu.ysu.ciallo.components.ScheduleCard
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomePage(
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
            Row(
                Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val features = listOf(
                    "成绩查询", "考试安排", "空闲教室", "其他功能"
                )
                features.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(it, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomePage() {
    // 预览时用Mock数据
    HomePage(useRemoteApi = false, onNavigateToLogin = {})
}
