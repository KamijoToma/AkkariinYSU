package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.edu.ysu.ciallo.gpa.GpaUiState
import cn.edu.ysu.ciallo.gpa.GpaDetail
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaCard(
    gpaState: GpaUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                IconButton(onClick = onRefresh) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh GPA"
                    )
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
                    Column {
                        gpaDetail.weightedAverageScoreOutsideCourses?.let { Text("校外课程加权平均分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.averageGpaQuery?.let { Text("平均绩点: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.programCreditType?.let { Text("方案学点类型: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.requiredCourseAverageGpa?.let { Text("必修课平均绩点: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.electiveCourseCreditsObtained?.let { Text("选修课获得学分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.requiredCourseFailedCredits?.let { Text("必修课不及格学分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.highestAverageGpa?.let { Text("平均绩点（最高）: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.outsideCourseCreditsObtained?.let { Text("校外课获得学分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.outsideCourseAverageGpaQuery?.let { Text("校外课平均绩点: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.trainingProgramName?.let { Text("培养方案名称: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.actualAverageScore?.let { Text("实际平均分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.outsideCourseHighestAverageGpa?.let { Text("校外课平均绩点（最高）: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.requiredCourseCreditsObtained?.let { Text("必修课获得学分: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.programCreditTypeDisplay?.let { Text("方案学点类型显示: $it", style = MaterialTheme.typography.bodyLarge) }
                        gpaDetail.weightedAverageScore?.let { Text("加权平均分: $it", style = MaterialTheme.typography.bodyLarge) }
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
    GpaCard(gpaState = GpaUiState.Loading, onRefresh = {})
}

@Preview
@Composable
fun PreviewGpaCardSuccess() {
    GpaCard(
        gpaState = GpaUiState.Success(
            GpaDetail(
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
        ),
        onRefresh = {}
    )
}

@Preview
@Composable
fun PreviewGpaCardError() {
    GpaCard(gpaState = GpaUiState.Error("网络错误"), onRefresh = {})
}

@Preview
@Composable
fun PreviewGpaCardIdle() {
    GpaCard(gpaState = GpaUiState.Idle, onRefresh = {})
}
