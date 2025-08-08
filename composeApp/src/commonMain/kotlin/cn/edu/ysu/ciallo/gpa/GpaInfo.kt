package cn.edu.ysu.ciallo.gpa

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * GPA 查询的响应数据模型。
 */
@Serializable
data class GpaResponse(
    val code: String,
    val msg: String? = null, // 尽管示例中没有，但通常会有 msg 字段
    val datas: GpaRawData? = null
)

@Serializable
data class GpaRawData(
    @SerialName("cxzxfaxfjd") val gpaQueryResult: GpaQueryResult? = null
)

@Serializable
data class GpaQueryResult(
    val totalSize: Int? = null,
    val pageSize: Int? = null,
    val rows: List<GpaDetail>? = null
)

/**
 * 绩点数据详情。
 */
@Serializable
data class GpaDetail(
    @SerialName("XWKJQPJF") val weightedAverageScoreOutsideCourses: Double? = null, // 校外课程加权平均分
    @SerialName("PPJDCX") val averageGpaQuery: Double? = null,     // 平均绩点（查询）
    @SerialName("FAXDLX") val programCreditType: String? = null,     // 方案学点类型
    @SerialName("BXKPPJD") val requiredCourseAverageGpa: Double? = null,   // 必修课平均绩点
    @SerialName("XXKHDXF") val electiveCourseCreditsObtained: Double? = null,   // 选修课获得学分
    @SerialName("BXKBJGXF") val requiredCourseFailedCredits: Double? = null, // 必修课不及格学分
    @SerialName("PPJDZG") val highestAverageGpa: Double? = null,     // 平均绩点（最高）
    @SerialName("XWKHDXF") val outsideCourseCreditsObtained: Double? = null,   // 校外课获得学分
    @SerialName("XWKPJJDCX") val outsideCourseAverageGpaQuery: Double? = null,// 校外课平均绩点（查询）
    @SerialName("PYFAMC") val trainingProgramName: String? = null,     // 培养方案名称
    @SerialName("SSPJF") val actualAverageScore: Double? = null,       // 实际平均分
    @SerialName("XWKPJJDZG") val outsideCourseHighestAverageGpa: Double? = null,// 校外课平均绩点（最高）
    @SerialName("BXKHDXF") val requiredCourseCreditsObtained: Double? = null,   // 必修课获得学分
    @SerialName("FAXDLX_DISPLAY") val programCreditTypeDisplay: String? = null, // 方案学点类型显示
    @SerialName("JQPJF") val weightedAverageScore: Double? = null        // 加权平均分
)

/**
 * 绩点查询的 UI 状态。
 */
sealed class GpaUiState {
    object Idle : GpaUiState()
    object Loading : GpaUiState()
    data class Success(val gpaDetail: GpaDetail) : GpaUiState()
    data class Error(val message: String) : GpaUiState()
}
