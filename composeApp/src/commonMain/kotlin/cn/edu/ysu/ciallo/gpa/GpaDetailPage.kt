package cn.edu.ysu.ciallo.gpa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 页面标题
        Text(
            text = "详细绩点信息",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        val displayValue: (Double?) -> String = { value ->
            value?.let { String.format("%.3f", it) } ?: "暂无数据"
        }

        // 综合绩点信息卡片
        GpaInfoCard(
            title = "综合绩点",
            icon = Icons.Default.Star,
            items = buildList {
                gpaDetail.averageGpaQuery?.let { 
                    add("平均绩点" to displayValue(it))
                }
                gpaDetail.highestAverageGpa?.let { 
                    add("平均绩点（最高）" to displayValue(it))
                }
                gpaDetail.weightedAverageScore?.let { 
                    add("加权平均分" to displayValue(it))
                }
                gpaDetail.actualAverageScore?.let { 
                    add("实际平均分" to displayValue(it))
                }
            }
        )

        // 分类绩点信息卡片
        GpaInfoCard(
            title = "分类绩点",
            icon = Icons.Default.School,
            items = buildList {
                gpaDetail.requiredCourseAverageGpa?.let { 
                    add("必修课平均绩点" to displayValue(it))
                }
                gpaDetail.outsideCourseAverageGpaQuery?.let { 
                    add("学位课平均绩点" to displayValue(it))
                }
                gpaDetail.outsideCourseHighestAverageGpa?.let { 
                    add("学位课平均绩点（最高）" to displayValue(it))
                }
                gpaDetail.weightedAverageScoreOutsideCourses?.let { 
                    add("学位课程加权平均分" to displayValue(it))
                }
            }
        )

        // 学分信息卡片
        GpaInfoCard(
            title = "学分信息",
            icon = Icons.AutoMirrored.Filled.Assignment,
            items = buildList {
                gpaDetail.requiredCourseCreditsObtained?.let { 
                    add("必修课获得学分" to displayValue(it))
                }
                gpaDetail.electiveCourseCreditsObtained?.let { 
                    add("选修课获得学分" to displayValue(it))
                }
                gpaDetail.outsideCourseCreditsObtained?.let { 
                    add("学位课获得学分" to displayValue(it))
                }
                gpaDetail.requiredCourseFailedCredits?.let { 
                    add("必修课不及格学分" to displayValue(it))
                }
            }
        )

        // 方案信息卡片
        GpaInfoCard(
            title = "培养方案",
            icon = Icons.Default.Info,
            items = buildList {
                gpaDetail.trainingProgramName?.let { 
                    add("培养方案名称" to it)
                }
                gpaDetail.programCreditType?.let { 
                    add("方案学点类型" to it)
                }
                gpaDetail.programCreditTypeDisplay?.let { 
                    add("方案学点类型显示" to it)
                }
            }
        )
    }
}

@Composable
private fun GpaInfoCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<Pair<String, String>>
) {
    if (items.isEmpty()) return
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 卡片标题
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            // 信息项列表
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items.forEachIndexed { index, (label, value) ->
                    GpaInfoItem(label = label, value = value)
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GpaInfoItem(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun PreviewGpaDetailPage() {
    MaterialTheme {
        GpaDetailPageContent(paddingValues = PaddingValues(0.dp), gpaDetail = mockGpaData)
    }
}
