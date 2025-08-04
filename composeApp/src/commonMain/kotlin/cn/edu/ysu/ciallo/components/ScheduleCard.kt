package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ScheduleCard(
    today: String,
    tomorrow: String,
    onMore: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp)) {
            Text("日程", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(8.dp))
            Text("今天：$today")
            Text("明天：$tomorrow")
            TextButton(onClick = onMore) { Text("更多") }
        }
    }
}

@Preview
@Composable
fun PreviewScheduleCard() {
    ScheduleCard(today = "数学分析 8:00-9:40", tomorrow = "无课程", onMore = {})
}
