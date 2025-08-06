package cn.edu.ysu.ciallo.studentinfo

import cn.edu.ysu.ciallo.ysu.StudentBaseInfo

interface StudentInfoRepository {
    suspend fun getStudentBaseInfo(): StudentInfoResult
}

sealed class StudentInfoResult {
    data class Success(val studentInfo: StudentBaseInfo) : StudentInfoResult()
    data class Failure(val error: StudentInfoError) : StudentInfoResult()
}

sealed class StudentInfoError {
    object NotLoggedIn : StudentInfoError()
    object NetworkError : StudentInfoError()
    object UnknownError : StudentInfoError()
    data class ApiError(val message: String) : StudentInfoError()
}

data class StudentInfoData(
    val studentInfo: StudentBaseInfo,
    val lastUpdate: Long
)
