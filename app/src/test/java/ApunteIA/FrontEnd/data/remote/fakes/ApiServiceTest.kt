package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.SubmitAttemptRequestDto
import ApunteIA.FrontEnd.fakes.FakeSessionStore
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Verifica que Retrofit lea las respuestas reales del backend (mismos JSON que
 * devuelve Spring Boot) y que el interceptor mande el JWT.
 */
class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var session: FakeSessionStore
    private lateinit var apiService: ApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        session = FakeSessionStore.loggedIn()
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(session))
            .build()
        apiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
            .create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `login parses the JWT response and is sent without token`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"token":"eyJ.abc","tokenType":"Bearer","expiresIn":7200,"userName":"Maya Rodríguez","userId":2,"email":"demo@apunteia.com","role":"STUDENT"}"""
            )
        )

        val result = apiService.login(LoginRequestDto("demo@apunteia.com", "123456"))

        assertEquals("eyJ.abc", result.token)
        assertEquals(2L, result.userId)
        assertNull(mockWebServer.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `getNotes sends the bearer token and parses NoteResponse`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """[{"id":22,"title":"JPA","content":"texto","summary":null,"userId":2,"courseId":1,"courseCode":"EIF411","processingStatus":"PENDING","createdAt":"2026-10-29T17:18:01.123456","updatedAt":"2026-10-29T17:18:01.123456"}]"""
            )
        )

        val notes = apiService.getNotes(2)

        val request = mockWebServer.takeRequest()
        assertEquals("Bearer jwt", request.getHeader("Authorization"))
        assertEquals("/notes?userId=2", request.path)
        assertEquals(22L, notes.first().id)
        assertEquals("PENDING", notes.first().processingStatus)
    }

    @Test
    fun `submitAttempt parses score and correct options`() = runBlocking {
        mockWebServer.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"id":7,"assessmentId":50,"assessmentTitle":"Quiz IA - JPA","userId":2,"status":"COMPLETED","score":66.67,"startedAt":"2026-10-29T23:00:00Z","completedAt":"2026-10-29T23:05:00Z","durationSeconds":300,
                   "answers":[{"questionIndex":0,"selectedOptionIndex":1,"correct":true,"correctOptionIndex":1},{"questionIndex":1,"selectedOptionIndex":null,"correct":false}]}"""
            )
        )

        val attempt = apiService.submitAttempt(7, SubmitAttemptRequestDto(listOf(1, null)))

        assertEquals("""{"answers":[1,null]}""", mockWebServer.takeRequest().body.readUtf8())
        assertEquals(66.67, attempt.score!!, 0.001)
        assertEquals(1, attempt.answers!![0].correctOptionIndex)
        assertNull(attempt.answers!![1].correctOptionIndex)
    }

    @Test
    fun `a 401 on a protected route clears the session`() {
        mockWebServer.enqueue(MockResponse().setResponseCode(401).setBody("""{"status":401,"message":"El token expiró"}"""))

        try {
            runBlocking { apiService.getCourses() }
        } catch (expected: HttpException) {
            assertEquals(401, expected.code())
        }

        assertEquals(1, session.unauthorizedCalls)
    }
}