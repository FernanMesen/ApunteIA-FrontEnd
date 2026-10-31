package ApunteIA.FrontEnd.data.remote

import ApunteIA.FrontEnd.data.util.backendMessage
import ApunteIA.FrontEnd.fakes.httpError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ApiErrorsTest {

    @Test
    fun `reads the message of the backend ErrorResponse`() {
        assertEquals("La nota no existe", httpError(404, "La nota no existe").backendMessage())
    }

    @Test
    fun `prefers the first field error on validation failures`() {
        val body = """{"status":400,"error":"Bad Request","message":"La petición tiene campos inválidos","path":"/auth/register","fieldErrors":{"password":"La contraseña debe tener entre 6 y 72 caracteres"}}"""
        val error = HttpException(Response.error<Any>(400, body.toResponseBody("application/json".toMediaType())))

        assertEquals("La contraseña debe tener entre 6 y 72 caracteres", error.backendMessage())
    }

    @Test
    fun `returns null for empty bodies or non HTTP errors`() {
        assertNull(httpError(500).backendMessage())
        assertNull(IllegalStateException("x").backendMessage())
    }
}