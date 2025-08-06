package cn.edu.ysu.ciallo.ysu

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StudentBaseInfo(
    @SerialName("XM") val name: String? = null, // 姓名
    @SerialName("XH") val studentId: String? = null, // 学号
    @SerialName("XBDM_DISPLAY") val gender: String? = null, // 性别
    @SerialName("CSRQ") val birthday: String? = null, // 出生日期
    @SerialName("MZDM_DISPLAY") val ethnicGroup: String? = null, // 民族
    @SerialName("ZZMMDM_DISPLAY") val politicalStatus: String? = null, // 政治面貌
    @SerialName("SFZJH") val idCardNumber: String? = null, // 身份证件号
    @SerialName("LXDH") val contactPhone: String? = null, // 联系电话
    @SerialName("DZXX") val email: String? = null, // 电子邮箱
    @SerialName("DWDM_DISPLAY") val department: String? = null, // 所在单位（学院）
    @SerialName("ZYDM_DISPLAY") val major: String? = null, // 专业
    @SerialName("BJDM_DISPLAY") val className: String? = null, // 班级
    @SerialName("RXNY") val enrollmentYearMonth: String? = null, // 入学年月
    @SerialName("XSLXDM_DISPLAY") val studentType: String? = null, // 学生类型
    @SerialName("XXXSDM_DISPLAY") val studyForm: String? = null, // 学习形式
    @SerialName("XQDM_DISPLAY") val campus: String? = null, // 校区
    @SerialName("JTDZ") val homeAddress: String? = null, // 家庭地址
    @SerialName("SG") val height: String? = null, // 身高
    @SerialName("TZ") val weight: Double? = null, // 体重
    @SerialName("JKZKDM_DISPLAY") val healthStatus: String? = null, // 健康状况
    @SerialName("XSZT") val studentStatus: String? = null, // 学生状态
    @SerialName("XSZT_DISPLAY") val studentStatusDisplay: String? = null, // 学生状态显示
    @SerialName("XSZP") val studentPhoto: String? = null // 学生照片
)
