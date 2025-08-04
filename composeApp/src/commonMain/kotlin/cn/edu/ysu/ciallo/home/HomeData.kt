package cn.edu.ysu.ciallo.home

/**
 * 首页各区块数据模型
 */
data class HomeData(
    val greeting: String,
    val studentType: String,
    val scheduleToday: String,
    val scheduleTomorrow: String,
    val electricityInfo: String,
    val bookInfo: String,
    val libraryStatus: List<LibraryStatus>,
    val cardBalance: String,
    val networkInfo: String
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

/**
 * 示例实现：本地假数据
 */
class FakeHomeRepository : HomeRepository {
    override suspend fun getHomeData(): HomeData = HomeData(
        greeting = "晚上好 祝你好梦",
        studentType = "本科生功能已经激活",
        scheduleToday = "暂无日程",
        scheduleTomorrow = "暂无日程",
        electricityInfo = "需要填写电费账号\n目前无法查询",
        bookInfo = "借书 0 本\n目前没有待归还图书",
        libraryStatus = listOf(
            LibraryStatus("南校区", 161, 4423),
            LibraryStatus("北校区", 78, 1021)
        ),
        cardBalance = "12.34",
        networkInfo = "获取校园网流量信息失败\n您还未输入账号密码？"
    )
}

