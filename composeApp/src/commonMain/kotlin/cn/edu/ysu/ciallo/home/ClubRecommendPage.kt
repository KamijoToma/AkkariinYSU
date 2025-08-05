package cn.edu.ysu.ciallo.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.ui.tooling.preview.Preview

object ClubRecommendPage : Tab {
    @Composable
    override fun Content() {
        ClubRecommendPageContent()
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(
            title = "社团推荐",
            icon = null,
            index = 1u
        )
}

@Composable
fun ClubRecommendPageContent(modifier: Modifier = Modifier) {
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
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B5BA9))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("社团 $idx", color = Color.White, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewClubRecommendPage() {
    ClubRecommendPageContent()
}
