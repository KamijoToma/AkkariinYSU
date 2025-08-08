package cn.edu.ysu.ciallo.gpa

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.edu.ysu.ciallo.components.mockGpaData
import org.jetbrains.compose.ui.tooling.preview.Preview

data class GpaDetailPage(
    val gpaDetail: GpaDetail
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("绩点详情") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            GpaDetailPageContent(paddingValues, gpaDetail)
        }
    }
}

@Composable
fun GpaDetailPageContent(
    paddingValues: PaddingValues,
    gpaDetail: GpaDetail
) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
    ) {
        Text(
            text = "详细绩点信息",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val displayValue: (Double?) -> String = { value ->
            value?.toString() ?: "N/A"
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gpaDetail.averageGpaQuery?.let {
                Text(
                    "平均绩点: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.weightedAverageScore?.let {
                Text(
                    "加权平均分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.actualAverageScore?.let {
                Text(
                    "实际平均分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.highestAverageGpa?.let {
                Text(
                    "平均绩点（最高）: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.requiredCourseAverageGpa?.let {
                Text(
                    "必修课平均绩点: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.outsideCourseAverageGpaQuery?.let {
                Text(
                    "校外课平均绩点: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.outsideCourseHighestAverageGpa?.let {
                Text(
                    "校外课平均绩点（最高）: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.weightedAverageScoreOutsideCourses?.let {
                Text(
                    "校外课程加权平均分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.requiredCourseCreditsObtained?.let {
                Text(
                    "必修课获得学分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.electiveCourseCreditsObtained?.let {
                Text(
                    "选修课获得学分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.outsideCourseCreditsObtained?.let {
                Text(
                    "校外课获得学分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.requiredCourseFailedCredits?.let {
                Text(
                    "必修课不及格学分: ${displayValue(it)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.programCreditType?.let {
                Text(
                    "方案学点类型: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.programCreditTypeDisplay?.let {
                Text(
                    "方案学点类型显示: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            gpaDetail.trainingProgramName?.let {
                Text(
                    "培养方案名称: $it",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewGpaDetailPage() {
    MaterialTheme {
        GpaDetailPageContent(paddingValues = PaddingValues(0.dp), gpaDetail = mockGpaData)
    }
}
