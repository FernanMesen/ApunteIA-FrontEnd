package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.domain.error.MainError
import ApunteIA.FrontEnd.domain.model.UserStats
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.io.IOException

class MainRepositoryImplTest {

    private lateinit var dataSource: MainDataSource
    private lateinit var repository: MainRepositoryImpl

    @Before
    fun setup() {
        dataSource = mock()
        repository = MainRepositoryImpl(dataSource)
    }

    @Test
    fun `getUserStats returns success with data`() = runBlocking {
        whenever(dataSource.getUserStats()).thenReturn(UserStats("Maya", 12, 53, 6, 6, 9))
        val result = repository.getUserStats()
        assertTrue(result.isSuccess)
        assertEquals("Maya", result.getOrNull()?.userName)
    }

    @Test
    fun `getUserStats maps IOException to MainError NoConnection`() = runBlocking {
        whenever(dataSource.getUserStats()).thenAnswer { throw IOException() }
        val result = repository.getUserStats()
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is MainError.NoConnection)
    }
}
//