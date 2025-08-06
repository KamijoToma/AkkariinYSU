package cn.edu.ysu.ciallo.studentinfo

import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory
import kotlinx.datetime.Clock

class RemoteStudentInfoRepository(
    private val api: YsuEhallApi = YsuEhallApiFactory.getInstance()
) : StudentInfoRepository {
    override suspend fun getStudentBaseInfo(): StudentInfoResult {
        return try {
            if (!api.isLoggedIn()) {
                return StudentInfoResult.Failure(StudentInfoError.NotLoggedIn)
            }

            val response = api.getStudentBaseInfo()

            if (response != null) {
                println("成功获取学生基本信息: ${response.name}")
                StudentInfoResult.Success(
                    StudentInfoData(
                        studentInfo = response,
                        lastUpdate = Clock.System.now().toEpochMilliseconds()
                    ).studentInfo
                )
            } else {
                println("获取学生基本信息失败，结果: $response")
                StudentInfoResult.Failure(StudentInfoError.UnknownError)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            StudentInfoResult.Failure(StudentInfoError.NetworkError)
        }
    }
}
