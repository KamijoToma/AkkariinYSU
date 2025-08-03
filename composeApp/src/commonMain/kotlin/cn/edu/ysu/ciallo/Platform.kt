package cn.edu.ysu.ciallo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform