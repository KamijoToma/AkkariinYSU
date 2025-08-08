package cn.edu.ysu.ciallo.gpa

import cn.edu.ysu.ciallo.ysu.YsuEhallApi

interface GpaRepository {
    suspend fun getGpaInfo(): GpaUiState
}

class RemoteGpaRepository(
    private val api: YsuEhallApi
) : GpaRepository {
    override suspend fun getGpaInfo(): GpaUiState{
        return try {
            // 进行 GPA 查询
            val response = api.getGpaInfo()
            if (response != null && response.code == "0" && response.datas?.gpaQueryResult?.rows?.isNotEmpty() == true) {
                println("成功获取绩点信息: ${response.datas.gpaQueryResult.rows.first()}")
                GpaUiState.Success(response.datas.gpaQueryResult.rows.first())
            } else {
                GpaUiState.Error(response?.msg ?: "获取绩点信息失败")
            }
        } catch (e: Exception) {
            GpaUiState.Error("网络请求失败: ${e.message}")
        }
    }
}

class MockGpaRepository : GpaRepository {
    override suspend fun getGpaInfo(): GpaUiState {
        return GpaUiState.Success(
            GpaDetail(
                weightedAverageScoreOutsideCourses = 88.0,
                averageGpaQuery = 3.8,
                programCreditType = "主修",
                requiredCourseAverageGpa = 3.7,
                electiveCourseCreditsObtained = 25.0,
                requiredCourseFailedCredits = 0.0,
                highestAverageGpa = 3.9,
                outsideCourseCreditsObtained = 40.0,
                outsideCourseAverageGpaQuery = 3.85,
                trainingProgramName = "2022级计算机科学与技术主修培养方案",
                actualAverageScore = 87.5,
                outsideCourseHighestAverageGpa = 3.95,
                requiredCourseCreditsObtained = 140.0,
                programCreditTypeDisplay = "主修",
                weightedAverageScore = 88.5
            )
        )
    }
}


