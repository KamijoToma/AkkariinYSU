package cn.edu.ysu.ciallo.cardbalance

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CardBalanceRepositoryTest {
    @Test
    fun testMockRepositorySuccess() = runTest {
        val repo = MockCardBalanceRepository()
        val result = repo.getCardBalance()
        assertTrue(result is CardBalanceResult.Success)
        val data = result.data
        assertEquals(123.45, data.balance)
    }

    @Test
    fun testMockRepositoryNotLoggedIn() = runTest {
        val repo = object : CardBalanceRepository {
            override suspend fun getCardBalance(): CardBalanceResult {
                return CardBalanceResult.Failure(CardBalanceError.NotLoggedIn)
            }
        }
        val result = repo.getCardBalance()
        assertTrue(result is CardBalanceResult.Failure)
        assertTrue(result.error is CardBalanceError.NotLoggedIn)
    }
}

