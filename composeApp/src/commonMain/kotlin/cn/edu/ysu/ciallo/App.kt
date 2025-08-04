package cn.edu.ysu.ciallo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.cardbalance.CardBalanceResult
import cn.edu.ysu.ciallo.cardbalance.CardBalanceViewModel
import cn.edu.ysu.ciallo.cardbalance.MockCardBalanceRepository
import cn.edu.ysu.ciallo.home.rememberHomeViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen() {
    var selectedTab by remember { mutableStateOf(0) }
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
            0 -> HomePage(Modifier.padding(innerPadding))
            1 -> ClubRecommendPage(Modifier.padding(innerPadding))
        }
    }
}

@Composable
fun HomePage(modifier: Modifier = Modifier) {
    val viewModel = rememberHomeViewModel()
    val data = viewModel.homeData.value

    // --- 卡余额模块 ---
    // true 使用真实API, false 使用模拟数据
    val useRemoteApi = true

    // 在使用远程API时，设置登录凭据
    // TODO: 在实际应用中，应通过登录界面获取并安全地存储这些信息
    if (useRemoteApi) {
        cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory.setCredentials("xxxxxxxxx", "xxxxxxxxx")
    }

    // 根据 useRemoteApi 标志选择不同的 Repository
    val cardBalanceViewModel = remember(useRemoteApi) {
        val repository = if (useRemoteApi) {
            cn.edu.ysu.ciallo.cardbalance.RemoteCardBalanceRepository()
        } else {
            MockCardBalanceRepository()
        }
        CardBalanceViewModel(repository)
    }
    val cardBalanceState = cardBalanceViewModel.uiState.value

    // 首次进入自动加载卡余额
    LaunchedEffect(Unit) { cardBalanceViewModel.loadCardBalance() }
    // --- 卡余额模块结束 ---

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
                // 顶部标题
                Text(
                    data.greeting,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                // 学生类型提示
                AssistChip(
                    onClick = {},
                    label = { Text(data.studentType) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                // 日程区块
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text("日程", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("今天：${data.scheduleToday}")
                        Text("明天：${data.scheduleTomorrow}")
                        TextButton(onClick = { /* TODO: 跳转日程 */ }) { Text("更多") }
                    }
                }
                // 电费、借书、图书馆
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(data.electricityInfo, fontSize = 14.sp)
                        Text(data.bookInfo, fontSize = 14.sp)
                        Text("图书馆当前状况", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            data.libraryStatus.forEach {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(it.campus, fontWeight = FontWeight.Bold)
                                    Text("在馆 ${it.people} 人", fontSize = 12.sp)
                                    Text("空位 ${it.seats} 个", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
                // 卡余额
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("卡余额", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            when (cardBalanceState) {
                                null -> Text("加载中...", fontSize = 14.sp)
                                is CardBalanceResult.Success -> Text(
                                    "￥${String.format("%.2f", cardBalanceState.data.balance)} (更新于${
                                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                            .format(java.time.Instant.ofEpochMilli(cardBalanceState.data.lastUpdate)
                                                .atZone(java.time.ZoneId.systemDefault())
                                                .toLocalDateTime())
                                    })",
                                    fontSize = 14.sp
                                )
                                is CardBalanceResult.Failure -> Text(
                                    when (val error = cardBalanceState.error) {
                                        is cn.edu.ysu.ciallo.cardbalance.CardBalanceError.LoginFailed -> "登录失败: ${error.reason}"
                                        cn.edu.ysu.ciallo.cardbalance.CardBalanceError.CaptchaRequired -> "需要验证码"
                                        cn.edu.ysu.ciallo.cardbalance.CardBalanceError.NotLoggedIn -> "未提供凭据"
                                        cn.edu.ysu.ciallo.cardbalance.CardBalanceError.NetworkError -> "网络错误"
                                        cn.edu.ysu.ciallo.cardbalance.CardBalanceError.UnknownError -> "未知错误"
                                        is cn.edu.ysu.ciallo.cardbalance.CardBalanceError.Custom -> error.message
                                    },
                                    color = Color.Red,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = { cardBalanceViewModel.loadCardBalance() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "刷新", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { /* TODO: 跳转到卡详情 */ }) {
                            Icon(Icons.Default.Details, contentDescription = "卡详情", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                // 校园网流量
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text(data.networkInfo, fontSize = 14.sp)
                    }
                }
                // 功能区按钮
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

@Composable
fun ClubRecommendPage(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        // 顶部返回与标题
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Home, contentDescription = "返回")
            Spacer(Modifier.size(8.dp))
            Text("社团推荐", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(10) { idx ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color(0xFF3B5BA9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("西电开源社区", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("技术豪杰与隐藏大佬无处不在", color = Color.White, fontSize = 12.sp)
                        }
                        Spacer(Modifier.size(8.dp))
                        Text("技术", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun App() {
    MainScreen()
}
