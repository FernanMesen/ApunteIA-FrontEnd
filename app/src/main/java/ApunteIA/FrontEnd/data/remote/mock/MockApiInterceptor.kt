package ApunteIA.FrontEnd.data.remote.mock

import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.NoteDto
import ApunteIA.FrontEnd.data.datasource.model.QuestionDto
import ApunteIA.FrontEnd.data.datasource.model.RecentActivityDto
import ApunteIA.FrontEnd.data.datasource.model.TopicDto
import ApunteIA.FrontEnd.data.datasource.model.UserStatsDto
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockApiInterceptor @Inject constructor() : Interceptor {

    private val gson = Gson()
    private val nextNoteId = AtomicInteger(100)
    private val nextTopicId = AtomicInteger(100)

    private val notes = mutableListOf(
        NoteDto("1", "Estereoquímica", "Química Orgánica", 5, "Escaneado 4 ago", "Resumen de enantiómeros y quiralidad.", "Débil"),
        NoteDto("2", "Ciclo Celular", "Biología Celular", 3, "Escaneado 1 ago", "Fases G1, S, G2 y M.", "Sólido")
    )

    private val topics = mutableListOf(
        TopicDto(1, "Estructura Atómica y Enlaces", "Química Orgánica", "Sólido", 100),
        TopicDto(2, "Alcanos y Nomenclatura", "Química Orgánica", "Sólido", 98),
        TopicDto(3, "Química Ácido-Base", "Química Orgánica", "Sólido", 96),
        TopicDto(4, "Estereoquímica", "Química Orgánica", "Débil", 50),
        TopicDto(5, "Mecanismos de Reacción (SN1/SN2)", "Química Orgánica", "Débil", 47),
        TopicDto(6, "Espectroscopía IR y RMN", "Química Orgánica", "Faltante", 0),
        TopicDto(7, "Ciclo Celular", "Biología Celular", "Sólido", 94),
        TopicDto(8, "Transporte en la Membrana Celular", "Biología Celular", "Débil", 45),
        TopicDto(9, "División Celular (Mitosis y Meiosis)", "Biología Celular", "Faltante", 0),
        TopicDto(10, "Límites e Infinito", "Cálculo II", "Faltante", 0)
    )

    private val questions = listOf(
        QuestionDto(1, "¿Qué término describe moléculas que son imágenes especulares entre sí pero no superponibles?",
            listOf("Enantiómeros", "Diastereómeros", "Confórmeros", "Isótopos"), 0,
            "Los enantiómeros son isómeros ópticos que son imágenes especulares no superponibles entre sí."),
        QuestionDto(2, "Una mezcla racémica contiene:",
            listOf("Partes iguales de ambos enantiómeros", "Solo el enantiómero R", "Un solo compuesto aquiral", "Dos diastereómeros en proporción 3:1"), 0,
            "Una mezcla racémica es una combinación 50:50 de ambos enantiómeros, por lo que no muestra rotación óptica neta."),
        QuestionDto(3, "¿Cuál es el ángulo de enlace aproximado en un átomo de carbono con hibridación sp3?",
            listOf("90°", "120°", "109.5°", "180°"), 2,
            "La geometría tetraédrica de la hibridación sp3 resulta en ángulos de enlace de 109.5°."),
        QuestionDto(4, "La regla de Markovnikov se aplica a:",
            listOf("Combustión", "Adición electrofílica", "Sustitución nucleofílica", "Eliminación"), 1,
            "La regla de Markovnikov predice el resultado de las reacciones de adición electrofílica a alquenos asimétricos."),
        QuestionDto(5, "¿Qué grupo funcional es característico de los alcoholes?",
            listOf("-CHO", "-COOH", "-OH", "-NH2"), 2,
            "El grupo hidroxilo (-OH) unido a un carbono saturado define a los alcoholes.")
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath.trimEnd('/')
        val method = request.method

        Thread.sleep(SIMULATED_LATENCY_MS)

        if (request.header(FORCE_ERROR_HEADER) == "true") {
            return buildResponse(request, 500, """{"message":"Simulated server error"}""")
        }

        val (code, bodyJson) = when {
            path.endsWith("/auth/login") && method == "POST" -> handleLogin(request)

            path.endsWith("/notes") && method == "GET" ->
                200 to gson.toJson(notes)

            path.endsWith("/notes") && method == "POST" -> {
                val created = readBody(request, NoteDto::class.java)
                    ?.copy(id = nextNoteId.getAndIncrement().toString())
                if (created != null) notes.add(created)
                201 to gson.toJson(created)
            }

            path.endsWith("/questions") && method == "GET" ->
                200 to gson.toJson(questions)

            path.endsWith("/topics") && method == "GET" ->
                200 to gson.toJson(topics)

            path.endsWith("/topics") && method == "POST" -> {
                val created = readBody(request, TopicDto::class.java)
                    ?.copy(id = nextTopicId.getAndIncrement())
                if (created != null) topics.add(created)
                201 to gson.toJson(created)
            }

            path.endsWith("/dashboard/stats") && method == "GET" ->
                200 to gson.toJson(
                    UserStatsDto(
                        userName = "Maya",
                        streakDays = 12,
                        progressPercent = 53,
                        notesCount = notes.size,
                        pendingTopicsCount = topics.count { it.status != "Sólido" },
                        quizzesDoneCount = 9
                    )
                )

            path.endsWith("/dashboard/activity") && method == "GET" ->
                200 to gson.toJson(
                    listOf(
                        RecentActivityDto("Escaneadas 3 páginas", "Mecanismos de Reacción", "Hace 2h"),
                        RecentActivityDto("Cuestionario completado", "Estereoquímica (4/5)", "Ayer"),
                        RecentActivityDto("Auditoría actualizada", "2 brechas nuevas", "Hace 2 días")
                    )
                )

            else ->
                404 to """{"message":"Mock endpoint not found: $method $path"}"""
        }

        return buildResponse(request, code, bodyJson)
    }

    private fun handleLogin(request: okhttp3.Request): Pair<Int, String> {
        val credentials = readBody(request, LoginRequestDto::class.java)
        return if (credentials?.email == DEMO_EMAIL && credentials.password == DEMO_PASSWORD) {
            200 to gson.toJson(LoginResponseDto(token = "mock-token-123", userName = "Maya"))
        } else {
            401 to """{"message":"Credenciales inválidas"}"""
        }
    }

    private fun buildResponse(request: okhttp3.Request, code: Int, bodyJson: String): Response {
        return Response.Builder()
            .code(code)
            .message(if (code in 200..299) "OK" else "Mock Error")
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .body(bodyJson.toResponseBody("application/json".toMediaType()))
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun <T> readBody(request: okhttp3.Request, clazz: Class<T>): T? {
        val buffer = okio.Buffer()
        val body = request.body ?: return null
        body.writeTo(buffer)
        return gson.fromJson(buffer.readUtf8(), clazz)
    }

    companion object {
        private const val SIMULATED_LATENCY_MS = 250L
        private const val FORCE_ERROR_HEADER = "X-Mock-Force-Error"
        const val DEMO_EMAIL = "demo@apunteia.com"
        const val DEMO_PASSWORD = "123456"
    }
}