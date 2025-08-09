package cn.edu.ysu.ciallo.library

import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory

interface LibrarySeatRepository {
    suspend fun getLibrarySeatOverview(): LibrarySeatResult
}

class RemoteLibrarySeatRepository : LibrarySeatRepository {
    private val api: YsuEhallApi = YsuEhallApiFactory.getInstance()

    override suspend fun getLibrarySeatOverview(): LibrarySeatResult {
        return try {
            val response = api.getLibrarySeatOverview()
            if (response != null && response.code == 0 && response.data != null) {
                LibrarySeatResult.Success(response.data)
            } else {
                LibrarySeatResult.Failure(
                    LibrarySeatError.Custom(response?.message ?: "获取座位信息失败")
                )
            }
        } catch (e: Exception) {
            LibrarySeatResult.Failure(LibrarySeatError.NetworkError)
        }
    }
}

class MockLibrarySeatRepository : LibrarySeatRepository {
    override suspend fun getLibrarySeatOverview(): LibrarySeatResult {
        val mockData = listOf(
            LibraryFloor(
                id = 2,
                name = "一层",
                totalCount = 374,
                remainCount = 373,
                spanDay = false,
                children = listOf(
                    LibraryArea(
                        id = 2,
                        name = "新书阅览区",
                        totalCount = 106,
                        remainCount = 105,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 4,
                        name = "电子阅览室",
                        totalCount = 98,
                        remainCount = 98,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 3,
                        name = "期刊报刊阅览室",
                        totalCount = 170,
                        remainCount = 170,
                        spanDay = false
                    )
                ),
                deptId = 3
            ),
            LibraryFloor(
                id = 3,
                name = "二层",
                totalCount = 1198,
                remainCount = 1196,
                spanDay = false,
                children = listOf(
                    LibraryArea(
                        id = 5,
                        name = "社会科学一区",
                        totalCount = 210,
                        remainCount = 210,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 6,
                        name = "社会科学二区",
                        totalCount = 118,
                        remainCount = 118,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 9,
                        name = "休闲阅读一区",
                        totalCount = 254,
                        remainCount = 253,
                        spanDay = false
                    )
                ),
                deptId = 3
            ),
            LibraryFloor(
                id = 4,
                name = "三层",
                totalCount = 1263,
                remainCount = 1250,
                spanDay = false,
                children = listOf(
                    LibraryArea(
                        id = 11,
                        name = "自然科学一区",
                        totalCount = 210,
                        remainCount = 210,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 14,
                        name = "自然科学四区",
                        totalCount = 306,
                        remainCount = 304,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 16,
                        name = "休闲阅读三区",
                        totalCount = 303,
                        remainCount = 294,
                        spanDay = false
                    )
                ),
                deptId = 3
            ),
            LibraryFloor(
                id = 5,
                name = "四层",
                totalCount = 862,
                remainCount = 860,
                spanDay = false,
                children = listOf(
                    LibraryArea(
                        id = 17,
                        name = "样本图书一区",
                        totalCount = 210,
                        remainCount = 210,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 18,
                        name = "样本图书二区",
                        totalCount = 118,
                        remainCount = 117,
                        spanDay = false
                    ),
                    LibraryArea(
                        id = 20,
                        name = "样本图书四区",
                        totalCount = 306,
                        remainCount = 305,
                        spanDay = false
                    )
                ),
                deptId = 3
            )
        )
        return LibrarySeatResult.Success(mockData)
    }
}
