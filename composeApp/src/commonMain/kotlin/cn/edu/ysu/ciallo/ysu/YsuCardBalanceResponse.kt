package cn.edu.ysu.ciallo.ysu

import kotlinx.serialization.Serializable

/**
 * 用于解析燕山大学E-hall一卡通余额API响应的JSON数据。
 * @property remining 余额，以字符串形式表示。
 * @property cardstatusname 卡状态，例如 "在用"。
 * @property availdate 卡的有效期。
 * @property code 响应状态码，200表示成功。
 */
@Serializable
data class YsuCardBalanceResponse(
    val remining: String? = null,
    val cardstatusname: String? = null,
    val availdate: String? = null,
    val code: Int
)

