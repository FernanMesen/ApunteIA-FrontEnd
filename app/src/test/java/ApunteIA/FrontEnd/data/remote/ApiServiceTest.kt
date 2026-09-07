package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getNotes success returns list`() = runBlocking {
        val json = """
            [
              {
                "id": "1",
                "title": "Estereoquímica",
                "className": "Química Orgánica",
                "classNumber": 5,
                "date": "Escaneado 4 ago",
                "preview": "Contenido de ejemplo",
                "status": "Débil"
              }
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val result = apiService.getNotes()

        assertEquals(1, result.size)
        assertEquals("Estereoquímica", result[0].title)
        assertEquals("1", result[0].id)
    }

    @Test
    fun `getNotes without id returns list with null id`() = runBlocking {
        val json = """
            [
              {
                "title": "Estereoquímica",
                "className": "Química Orgánica",
                "classNumber": 5,
                "date": "Escaneado 4 ago",
                "preview": "Contenido de ejemplo",
                "status": "Débil"
              }
            ]
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(json).setResponseCode(200))

        val result = apiService.getNotes()

        assertEquals(1, result.size)
        assertEquals("Estereoquímica", result[0].title)
        assertEquals(null, result[0].id)
    }

    @Test
    fun `createNote success returns created note`() = runBlocking {
        val noteDto = NoteDto("1", "Test", "Subject", 1, "Date", "Preview", "Status")
        val json = """
            {
              "id": "1",
              "title": "Test",
              "className": "Subject",
              "classNumber": 1,
              "date": "Date",
              "preview": "Preview",
              "status": "Status"
            }
        """.trimIndent()
        mockWebServer.enqueue(MockResponse().setBody(json).setResponseCode(201))

        val result = apiService.createNote(noteDto)

        assertEquals("Test", result.title)
        assertEquals("1", result.id)
    }
}
