package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.components.CardBalanceCard
import cn.edu.ysu.ciallo.components.LibraryCard
import cn.edu.ysu.ciallo.components.NetworkCard
import cn.edu.ysu.ciallo.components.ScheduleCard
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    useRemoteApi: Boolean = true
) {
    val viewModel = rememberHomeViewModel()
    val data = viewModel.homeData.value
    if (useRemoteApi) {
        cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory.setCredentials("xxxxxxxxx", "xxxxxxxxx")
    }
    val cardBalanceViewModel = remember(useRemoteApi) {
        val repository = if (useRemoteApi) {
            cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository()
        } else {
            MockCardBalanceRepository()
        }
        CardBalanceViewModel(repository)
    }
    val cardBalanceState = cardBalanceViewModel.uiState.value
    LaunchedEffect(Unit) { cardBalanceViewModel.loadCardBalance() }
    if (data == null) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    Box(modifier = modifier.fillMaxSize()) {
        androidx.compose.foundation.rememberScrollState().let { scrollState ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    data.greeting,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                AssistChip(
                    onClick = {},
                    label = { Text(data.studentType) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                ScheduleCard(
                    today = data.scheduleToday,
                    tomorrow = data.scheduleTomorrow,
                    onMore = { /* TODO: 跳转日程 */ }
                )
                LibraryCard(
                    electricityInfo = data.electricityInfo,
                    bookInfo = data.bookInfo,
                    libraryStatus = data.libraryStatus
                )
                CardBalanceCard(
                    cardBalanceState = cardBalanceState,
                    onRefresh = { cardBalanceViewModel.loadCardBalance() },
                    onDetails = { /* TODO: 跳转到卡详情 */ }
                )
                NetworkCard(
                    networkInfo = data.networkInfo
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
}

@Preview
@Composable
fun PreviewHomePage() {
    // 预览时用Mock数据
    HomePage(useRemoteApi = false)
}

