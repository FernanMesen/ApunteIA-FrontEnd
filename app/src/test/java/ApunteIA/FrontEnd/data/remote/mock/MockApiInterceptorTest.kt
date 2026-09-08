package ApunteIA.FrontEnd.data.remote.mock

import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MockApiInterceptorTest {

    private val gson = Gson()
    private lateinit var client: OkHttpClient

    @Before
    fun setup() {
        client = OkHttpClient.Builder()
            .addInterceptor(MockApiInterceptor())
            .build()
    }

    @Test
    fun `getTopics returns seeded topics`() {
        val request = Request.Builder().url("http://mock.local/topics").get().build()
        val response = client.newCall(request).execute()
        val topics = gson.fromJson(response.body!!.string(), Array<TopicDto>::class.java)

        assertEquals(200, response.code)
        assertTrue(topics.isNotEmpty())
    }

    @Test
    fun `createTopic is reflected in a later getTopics call`() {
        val newTopic = TopicDto(id = 0, title = "Introducción al curso", className = "Curso Nuevo", status = "Faltante", progress = 0)
        val createRequest = Request.Builder()
            .url("http://mock.local/topics")
            .post(gson.toJson(newTopic).toRequestBody("application/json".toMediaType()))
            .build()
        client.newCall(createRequest).execute()

        val listRequest = Request.Builder().url("http://mock.local/topics").get().build()
        val response = client.newCall(listRequest).execute()
        val topics = gson.fromJson(response.body!!.string(), Array<TopicDto>::class.java)

        assertTrue(topics.any { it.className == "Curso Nuevo" })
    }

    @Test
    fun `login with valid credentials returns 200`() {
        val credentials = LoginRequestDto(MockApiInterceptor.DEMO_EMAIL, MockApiInterceptor.DEMO_PASSWORD)
        val request = Request.Builder()
            .url("http://mock.local/auth/login")
            .post(gson.toJson(credentials).toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(200, response.code)
    }

    @Test
    fun `login with invalid credentials returns 401`() {
        val credentials = LoginRequestDto("wrong@user.com", "badpass")
        val request = Request.Builder()
            .url("http://mock.local/auth/login")
            .post(gson.toJson(credentials).toRequestBody("application/json".toMediaType()))
            .build()

        val response = client.newCall(request).execute()
        assertEquals(401, response.code)
    }

    @Test
    fun `forced error header returns 500`() {
        val request = Request.Builder()
            .url("http://mock.local/topics")
            .header("X-Mock-Force-Error", "true")
            .get()
            .build()

        val response = client.newCall(request).execute()
        assertEquals(500, response.code)
    }

    @Test
    fun `unknown path returns 404`() {
        val request = Request.Builder().url("http://mock.local/unknown-endpoint").get().build()
        val response = client.newCall(request).execute()
        assertEquals(404, response.code)
    }
}