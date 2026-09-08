package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.MainDataSource
import ApunteIA.FrontEnd.domain.model.UserStats
import ApunteIA.FrontEnd.domain.util.Resource
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
    fun `getUserStats returns Resource Success with data`() = runBlocking {
        whenever(dataSource.getUserStats()).thenReturn(UserStats("Maya", 12, 53, 6, 6, 9))

        val result = repository.getUserStats()

        assertTrue(result is Resource.Success)
        assertEquals("Maya", (result as Resource.Success).data.userName)
    }

    @Test
    fun `getUserStats returns Resource Error on IOException`() = runBlocking {
        whenever(dataSource.getUserStats()).thenAnswer { throw IOException() }

        val result = repository.getUserStats()

        assertTrue(result is Resource.Error)
    }
}