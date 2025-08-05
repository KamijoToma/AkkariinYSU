package cn.edu.ysu.ciallo.home

import cn.edu.ysu.ciallo.ysu.YsuEhallApi
import cn.edu.ysu.ciallo.ysu.YsuEhallApiFactory

/**
 * 首页各区块数据模型
 */
data class HomeData(
    val userName: String,
    val studentType: String,
    val scheduleToday: String,
    val scheduleTomorrow: String,
    val electricityInfo: String,
    val bookInfo: String,
    val libraryStatus: List<LibraryStatus>,
    val networkInfo: String,
    val logged: Boolean
)

data class LibraryStatus(
    val campus: String,
    val people: Int,
    val seats: Int
)

/**
 * 首页数据仓库接口
 */
interface HomeRepository {
    suspend fun getHomeData(): HomeData
}

val DEFAULT_HOME_DATA = HomeData(
    userName = "阿卡林",
    studentType = "本科生",
    scheduleToday = "暂无日程",
    scheduleTomorrow = "暂无日程",
    electricityInfo = "需要填写电费账号\n目前无法查询",
    bookInfo = "借书 0 本\n目前没有待归还图书",
    libraryStatus = listOf(
        LibraryStatus("南校区", 161, 4423),
        LibraryStatus("北校区", 78, 1021)
    ),
    networkInfo = "获取校园网流量信息失败\n您还未输入账号密码？",
    logged = true
)

/**
 * 示例实现：本地假数据
 */
class FakeHomeRepository : HomeRepository {
    override suspend fun getHomeData(): HomeData = DEFAULT_HOME_DATA
}

/**
 * 远程实现：通过API获取首页数据
 * 注意：实际实现需要根据具体API进行调整
 */
class RemoteHomeRepository : HomeRepository {
    // 假设有一个API客户端实例
    private val api: YsuEhallApi = YsuEhallApiFactory.getInstance()

    override suspend fun getHomeData(): HomeData {
        // 这里需要调用实际的API获取数据
        // 例如：api.getHomeData()，返回一个 HomeData 对象
        // 由于没有具体的API实现，这里返回假数据
        var logged = true
        var studentType = "<UNK>";
        var userName = "阿卡林";
        val user_info = api.getLoginUser()
        if (user_info != null && user_info.data != null) {
            logged = true
            studentType = user_info.data.categoryName ?: "<UNK>"
            userName = user_info.data.userName ?: "Akari"
        } else {
            logged = false
            studentType = "<未登录>"
        }

        return HomeData(
            userName = userName,
            studentType = studentType,
            scheduleToday = "暂无日程",
            scheduleTomorrow = "暂无日程",
            electricityInfo = "需要填写电费账号\n目前无法查询",
            bookInfo = "借书 0 本\n目前没有待归还图书",
            libraryStatus = listOf(
                LibraryStatus("南校区", 161, 4423),
                LibraryStatus("北校区", 78, 1021)
            ),
            networkInfo = "获取校园网流量信息失败\n您还未输入账号密码？",
            logged = logged
        )
    }
}

