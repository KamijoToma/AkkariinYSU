package cn.edu.ysu.ciallo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import org.jetbrains.compose.ui.tooling.preview.Preview

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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FA))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 顶部标题
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("晚上好 祝你好梦", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        // 研究生功能激活提示
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Text("本科生功能已经激活", modifier = Modifier.padding(16.dp))
        }
        // 日程区块
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("当前 暂无日程", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("明天 暂无日程")
                Spacer(Modifier.height(4.dp))
                Text("更多")
            }
        }
        // 电费账单、借书、图书馆状态等区块
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("需要填写电费账号\n目前无法查询")
                Spacer(Modifier.height(8.dp))
                Text("借书 0 本\n目前没有待归还图书")
                Spacer(Modifier.height(8.dp))
                Text("图书馆当前状况")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column { Text("南校区\n在馆 161 人\n空位 4423 个") }
                    Column { Text("北校区\n在馆 78 人\n空位 1021 个") }
                }
            }
        }
        // 卡里余额
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("卡里 0.00 元 查询\n卡通流水")
            }
        }
        // 校园流量信息失败
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Home, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text("获取校园网流量信息失败\n您还未输入账号密码？")
            }
        }
        // 功能区按钮
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Home, contentDescription = null)
                Text("成绩查询", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Home, contentDescription = null)
                Text("考试安排", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Home, contentDescription = null)
                Text("空闲教室", fontSize = 12.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Home, contentDescription = null)
                Text("其他功能", fontSize = 12.sp)
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
