package cn.edu.ysu.ciallo.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.edu.ysu.ciallo.library.LibraryFloor
import cn.edu.ysu.ciallo.library.LibrarySeatError
import cn.edu.ysu.ciallo.library.LibrarySeatResult
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LibraryCard(
    librarySeatState: LibrarySeatResult?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.LibraryBooks,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "图书馆座位概览",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(8.dp))
                when (librarySeatState) {
                    null -> Text("加载中...", fontSize = 14.sp)
                    is LibrarySeatResult.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            items(librarySeatState.data) { floor ->
                                FloorSeatInfo(floor = floor)
                            }
                        }
                    }
                    is LibrarySeatResult.Failure -> {
                        Text(
                            when (val error = librarySeatState.error) {
                                LibrarySeatError.NetworkError -> "网络错误"
                                LibrarySeatError.UnknownError -> "未知错误"
                                is LibrarySeatError.Custom -> error.message
                            },
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            IconButton(onClick = onRefresh) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "刷新",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FloorSeatInfo(floor: LibraryFloor) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(80.dp)
    ) {
        Text(
            floor.name,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            "总计 ${floor.totalCount}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "空闲 ${floor.remainCount}",
            fontSize = 12.sp,
            color = if (floor.remainCount > 0) MaterialTheme.colorScheme.primary else Color.Red,
            fontWeight = FontWeight.Medium
        )
        val occupancyRate = if (floor.totalCount > 0) {
            ((floor.totalCount - floor.remainCount).toFloat() / floor.totalCount * 100).toInt()
        } else 0
        Text(
            "使用率 ${occupancyRate}%",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun PreviewLibraryCard_Loading() {
    LibraryCard(
        librarySeatState = null,
        onRefresh = {}
    )
}

@Preview
@Composable
fun PreviewLibraryCard_Success() {
    val mockFloors = listOf(
        LibraryFloor(
            id = 2,
            name = "一层",
            totalCount = 374,
            remainCount = 373,
            spanDay = false,
            deptId = 3
        ),
        LibraryFloor(
            id = 3,
            name = "二层",
            totalCount = 1198,
            remainCount = 1196,
            spanDay = false,
            deptId = 3
        ),
        LibraryFloor(
            id = 4,
            name = "三层",
            totalCount = 1263,
            remainCount = 1250,
            spanDay = false,
            deptId = 3
        ),
        LibraryFloor(
            id = 5,
            name = "四层",
            totalCount = 862,
            remainCount = 860,
            spanDay = false,
            deptId = 3
        )
    )
    LibraryCard(
        librarySeatState = LibrarySeatResult.Success(mockFloors),
        onRefresh = {}
    )
}

@Preview
@Composable
fun PreviewLibraryCard_Error() {
    LibraryCard(
        librarySeatState = LibrarySeatResult.Failure(LibrarySeatError.NetworkError),
        onRefresh = {}
    )
}
