package cn.edu.ysu.ciallo.studentinfo

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cn.edu.ysu.ciallo.di.previewModule
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinApplicationPreview
import org.koin.compose.koinInject

class StudentInfoPage : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content(
    ) {
        val studentInfoViewModel: StudentInfoViewModel = koinInject()
        val studentInfoUiState = studentInfoViewModel.uiState.collectAsState().value
        LaunchedEffect(Unit) {
            studentInfoViewModel.loadStudentInfo()
        }
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("个人信息") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                )
            }
        ) { paddingValues ->
            StudentInfoPageContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                uiState = studentInfoUiState
            )
        }
    }
}

@Composable
fun InfoItem(label: String, value: String?) {
    if (value != null && value.isNotBlank()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentInfoPageContent(
    modifier: Modifier,
    uiState: StudentInfoUiState
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is StudentInfoUiState.Loading -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "正在加载个人信息...", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is StudentInfoUiState.Success -> {
                    val studentInfo = uiState.studentInfo
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "基本信息",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    InfoItem("姓名", studentInfo.name)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("学号", studentInfo.studentId)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("性别", studentInfo.gender)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("出生日期", studentInfo.birthday)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("民族", studentInfo.ethnicGroup)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("政治面貌", studentInfo.politicalStatus)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("身份证件号", studentInfo.idCardNumber)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "联系方式",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    InfoItem("联系电话", studentInfo.contactPhone)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("电子邮箱", studentInfo.email)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("家庭地址", studentInfo.homeAddress)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "学籍信息",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    InfoItem("所在单位（学院）", studentInfo.department)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("专业", studentInfo.major)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("班级", studentInfo.className)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("入学年月", studentInfo.enrollmentYearMonth)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("学生类型", studentInfo.studentType)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("学习形式", studentInfo.studyForm)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("校区", studentInfo.campus)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("学生状态", studentInfo.studentStatusDisplay)
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "健康信息",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                    InfoItem("身高", studentInfo.height)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("体重", studentInfo.weight?.toString())
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                    InfoItem("健康状况", studentInfo.healthStatus)
                                }
                            }
                        }
                        // 学生照片字段 XSZP 暂时不展示，因为需要额外的图片加载逻辑
                    }
                }

                is StudentInfoUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        // 可以考虑添加一个刷新按钮
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewStudentInfoPage() {
    KoinApplicationPreview(application = { modules(previewModule) }) {
        StudentInfoPageContent(
            modifier = Modifier.fillMaxSize(), uiState = StudentInfoUiState.Success(
                mockStudentInfo
            )
        )
    }
}
