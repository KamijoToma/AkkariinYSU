package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.edu.ysu.ciallo.di.previewModule
import cn.edu.ysu.ciallo.gpa.GpaDetail
import cn.edu.ysu.ciallo.gpa.GpaUiState
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCard(
    gpaState: GpaUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    onNavigateToDetail: (GpaDetail) -> Unit,
) {
    var isBlurred by remember { mutableStateOf(false) } // 用于控制是否模糊显示绩点
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = { /* No action on click, as per user's request */ }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "成绩查询",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isBlurred = !isBlurred }) {
                        Icon(
                            imageVector = if (isBlurred) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isBlurred) "显示绩点" else "隐藏绩点"
                        )
                    }
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新 GPA"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            when (gpaState) {
                is GpaUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    Text("加载中...", modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is GpaUiState.Success -> {
                    val gpaDetail = gpaState.gpaDetail
                    val displayValue: (Double?) -> String = { value ->
                        if (isBlurred) "XX.XX" else value?.toString() ?: "N/A"
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "平均绩点",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = displayValue(gpaDetail.averageGpaQuery),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "加权平均分",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = displayValue(gpaDetail.weightedAverageScore),
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onNavigateToDetail(gpaDetail) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("查看详情")
                    }
                }
                is GpaUiState.Error -> {
                    Text("加载失败: ${gpaState.message}", color = MaterialTheme.colorScheme.error)
                }
                is GpaUiState.Idle -> {
                    Text("点击刷新获取绩点信息", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewGpaCardLoading() {
    GpaCard(gpaState = GpaUiState.Loading, onRefresh = {}, onNavigateToDetail = {})
}

@Preview
@Composable
fun PreviewGpaCardSuccess() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        GpaCard(
            gpaState = GpaUiState.Success(
                mockGpaData
            ),
            onRefresh = {},
            onNavigateToDetail = {}
        )
    }
}

@Preview
@Composable
fun PreviewGpaCardError() {
    GpaCard(gpaState = GpaUiState.Error("网络错误"), onRefresh = {}, onNavigateToDetail = {})
}

@Preview
@Composable
fun PreviewGpaCardIdle() {
    GpaCard(gpaState = GpaUiState.Idle, onRefresh = {}, onNavigateToDetail = {})
}

val mockGpaData = GpaDetail(
    weightedAverageScoreOutsideCourses = 3.0,
    averageGpaQuery = 114.514,
    programCreditType = "本科",
    requiredCourseAverageGpa = 3.6,
    electiveCourseCreditsObtained = 10.0,
    requiredCourseFailedCredits = 0.0,
    highestAverageGpa = -1.0,
    outsideCourseCreditsObtained = 5.0,
    outsideCourseAverageGpaQuery = 3.2,
    trainingProgramName = "大胃袋科学与技术",
    actualAverageScore = 85.0,
    outsideCourseHighestAverageGpa = 3.3,
    requiredCourseCreditsObtained = 100.0,
    programCreditTypeDisplay = "本科",
    weightedAverageScore = 88.0
)