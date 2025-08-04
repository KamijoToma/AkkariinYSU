package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.home.LibraryStatus
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LibraryCard(
    electricityInfo: String,
    bookInfo: String,
    libraryStatus: List<LibraryStatus>
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(electricityInfo, fontSize = 14.sp)
            Text(bookInfo, fontSize = 14.sp)
            Text("图书馆当前状况", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                libraryStatus.forEach {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(it.campus, fontWeight = FontWeight.Bold)
                        Text("在馆 ${it.people} 人", fontSize = 12.sp)
                        Text("空位 ${it.seats} 个", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewLibraryCard() {
    LibraryCard(
        electricityInfo = "电费：剩余 23.5 元",
        bookInfo = "借书：还剩 2 本未归还",
        libraryStatus = listOf(
            LibraryStatus("东校区", 120, 80),
            LibraryStatus("西校区", 90, 60)
        )
    )
}
