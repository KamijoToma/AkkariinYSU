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
                TopAppBar(
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "$label:", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.width(8.dp))
            Text(text = value, style = MaterialTheme.typography.bodyLarge)
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
        modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            when (uiState) {
                is StudentInfoUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is StudentInfoUiState.Success -> {
                    val studentInfo = uiState.studentInfo
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item { InfoItem("姓名", studentInfo.name) }
                        item { InfoItem("学号", studentInfo.studentId) }
                        item { InfoItem("性别", studentInfo.gender) }
                        item { InfoItem("出生日期", studentInfo.birthday) }
                        item { InfoItem("民族", studentInfo.ethnicGroup) }
                        item { InfoItem("政治面貌", studentInfo.politicalStatus) }
                        item { InfoItem("身份证件号", studentInfo.idCardNumber) }
                        item { InfoItem("联系电话", studentInfo.contactPhone) }
                        item { InfoItem("电子邮箱", studentInfo.email) }
                        item { InfoItem("所在单位（学院）", studentInfo.department) }
                        item { InfoItem("专业", studentInfo.major) }
                        item { InfoItem("班级", studentInfo.className) }
                        item { InfoItem("入学年月", studentInfo.enrollmentYearMonth) }
                        item { InfoItem("学生类型", studentInfo.studentType) }
                        item { InfoItem("学习形式", studentInfo.studyForm) }
                        item { InfoItem("校区", studentInfo.campus) }
                        item { InfoItem("家庭地址", studentInfo.homeAddress) }
                        item { InfoItem("身高", studentInfo.height) }
                        item { InfoItem("体重", studentInfo.weight?.toString()) }
                        item { InfoItem("健康状况", studentInfo.healthStatus) }
                        item { InfoItem("学生状态", studentInfo.studentStatusDisplay) }
                        // 学生照片字段 XSZP 暂时不展示，因为需要额外的图片加载逻辑
                    }
                }

                is StudentInfoUiState.Error -> {
                    Text(
                        text = uiState.message,
                        color = MaterialTheme.colorScheme.error
                    )
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
