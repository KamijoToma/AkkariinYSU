package cn.edu.ysu.ciallo.library

import kotlinx.serialization.Serializable

@Serializable
data class LibrarySeatOverviewResponse(
    val code: Int,
    val message: String,
    val data: List<LibraryFloor>? = null,
    val count: Int = 0,
    val vals: String? = null
)

@Serializable
data class LibraryFloor(
    val id: Int,
    val name: String,
    val totalCount: Int,
    val remainCount: Int,
    val spanDay: Boolean,
    val children: List<LibraryArea>? = null,
    val deptId: Int? = null,
    val onlyUsedByDept: Boolean = false
)

@Serializable
data class LibraryArea(
    val id: Int,
    val name: String,
    val totalCount: Int,
    val remainCount: Int,
    val spanDay: Boolean,
    val property: Int = 0,
    val onlyUsedByDept: Boolean = false
)

sealed class LibrarySeatResult {
    data class Success(val data: List<LibraryFloor>) : LibrarySeatResult()
    data class Failure(val error: LibrarySeatError) : LibrarySeatResult()
}

sealed class LibrarySeatError {
    object NetworkError : LibrarySeatError()
    object UnknownError : LibrarySeatError()
    data class Custom(val message: String) : LibrarySeatError()
}
