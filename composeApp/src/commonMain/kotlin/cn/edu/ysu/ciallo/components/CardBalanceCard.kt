package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.cardbalance.CardBalanceData
import cn.edu.ysu.ciallo.cardbalance.CardBalanceError
import cn.edu.ysu.ciallo.cardbalance.CardBalanceResult
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.time.format.DateTimeFormatter

@Composable
fun CardBalanceCard(
    cardBalanceState: CardBalanceResult?,
    onRefresh: () -> Unit,
    onDetails: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CreditCard, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Column {
                Text("校园卡余额", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                            is CardBalanceError.LoginFailed -> "登录失败: ${error.reason}"
                            CardBalanceError.CaptchaRequired -> "需要验证码"
                            CardBalanceError.NotLoggedIn -> "未登录"
                            CardBalanceError.NetworkError -> "网络错误"
                            CardBalanceError.UnknownError -> "未知错误"
                            is CardBalanceError.Custom -> error.message
                        },
                        color = Color.Red,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = "刷新", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDetails) {
                Icon(Icons.Default.Details, contentDescription = "卡详情", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview
@Composable
fun PreviewCardBalanceCard_Loading() {
    CardBalanceCard(cardBalanceState = null, onRefresh = {}, onDetails = {})
}

@Preview
@Composable
fun PreviewCardBalanceCard_Success() {
    CardBalanceCard(
        cardBalanceState = CardBalanceResult.Success(
            CardBalanceData(balance = 123.45, lastUpdate = System.currentTimeMillis())
        ),
        onRefresh = {},
        onDetails = {}
    )
}

@Preview
@Composable
fun PreviewCardBalanceCard_Error() {
    CardBalanceCard(
        cardBalanceState = CardBalanceResult.Failure(CardBalanceError.NetworkError),
        onRefresh = {},
        onDetails = {}
    )
}
