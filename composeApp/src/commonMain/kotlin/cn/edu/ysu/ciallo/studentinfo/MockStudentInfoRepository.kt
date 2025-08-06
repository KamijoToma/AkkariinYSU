package cn.edu.ysu.ciallo.studentinfo

import cn.edu.ysu.ciallo.ysu.StudentBaseInfo
import kotlinx.datetime.Clock

val mockStudentInfo = StudentInfoData(
    studentInfo = StudentBaseInfo(
        name = "张三",
        studentId = "S123456789",
        gender = "男",
        birthday = "2000-01-01",
        ethnicGroup = "汉族",
        politicalStatus = "共青团员",
        idCardNumber = "11010120000101123X",
        contactPhone = "13812345678",
        email = "zhangsan@example.com",
        department = "计算机学院",
        major = "软件工程",
        className = "软件2022-1班",
        enrollmentYearMonth = "2022-09",
        studentType = "本科生",
        studyForm = "全日制",
        campus = "主校区",
        homeAddress = "某省某市某区某街道",
        height = "175",
        weight = 65.0,
        healthStatus = "良好",
        studentStatusDisplay = "在读",
        studentPhoto = null
    ),
    lastUpdate = Clock.System.now().toEpochMilliseconds()
).studentInfo


class MockStudentInfoRepository : StudentInfoRepository {
    override suspend fun getStudentBaseInfo(): StudentInfoResult {
        return StudentInfoResult.Success(
            mockStudentInfo
        )
    }
}
