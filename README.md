# ApunteIA — FrontEnd

FrontEnd del proyecto **APUNTE IA**: una app Android nativa para escanear apuntes, organizarlos por materia, generar cuestionarios de refuerzo y auditar brechas de conocimiento.

---

## 🧱 Stack técnico

| Tecnología | Versión | Uso |
|---|---|---|
| Kotlin | — | Lenguaje |
| Android Gradle Plugin (AGP) | 8.13.2 | Build system |
| Jetpack Compose | BOM 2024.04.01 | UI declarativa (toda la app, sin XML de layouts) |
| Navigation Compose | 2.7.7 | Navegación entre pantallas |
| Hilt | 2.51.1 | Inyección de dependencias |
| Retrofit + OkHttp | 2.9.0 / 4.12.0 | Cliente HTTP |
| Gson | — | Serialización JSON |
| CameraX | 1.3.4 | Captura de fotos en el flujo de escaneo |
| Coil | 2.6.0 | Carga de imágenes |
| Mockoon | — | Backend simulado (API REST) |
| JUnit / MockWebServer / Mockito | — | Testing |

**Configuración:**
- Template: Empty Activity
- Language: Kotlin
- Minimum SDK: **API 24** (`minSdk`)
- Target/Compile SDK: **34**
- UI: **Jetpack Compose**
- JDK requerido: **JDK 17** (el que espera AGP 8.13.x; asegurate de tenerlo seleccionado en Android Studio → File → Project Structure → SDK Location)

---

## 🏗️ Arquitectura

El proyecto sigue **Clean Architecture** en 3 capas, con flujo de dependencias en una sola dirección:

```
presentation → domain → data
```

`domain` es Kotlin puro (no conoce Android, Retrofit ni Compose) y define **contratos** (`repository/`, interfaces). `data` implementa esos contratos contra la red. `presentation` solo consume `domain` a través de ViewModels con `StateFlow`.

```
UI (Compose) → ViewModel → UseCase → Repository (interfaz) → RepositoryImpl → DataSource → ApiService (Retrofit) → Mockoon
```

### Estructura de carpetas

```
app/src/main/java/ApunteIA/FrontEnd/
│
├── presentation/                  # Todo lo visual y de estado de pantalla
│   ├── ui/
│   │   ├── screens/                # Una pantalla Compose por feature (Login, Notes, Quiz, Scan*, etc.)
│   │   ├── components/             # Composables reutilizables (NoteItem, TopicItem, TopHeader...)
│   │   ├── layout/                 # MainLayout.kt (scaffold + bottom nav)
│   │   └── theme/                  # Color.kt, Type.kt, Theme.kt (Material 3)
│   ├── viewmodel/                  # Un @HiltViewModel por feature, expone StateFlow<UiState>
│   ├── navigation/                 # Routes.kt (rutas) + AppNavigation.kt (NavHost)
│   └── model/                      # Modelos que solo existen en la UI (ScanModels.kt)
│
├── domain/                        # Reglas de negocio puras, sin dependencias de Android
│   ├── model/                      # Entidades de dominio (Note, Topic, Course, Question...)
│   ├── repository/                 # Interfaces (contratos) que implementa `data`
│   ├── usecase/                    # Una clase por acción de negocio (GetNotesUseCase, LoginUseCase...)
│   ├── error/                      # Errores tipados por feature (AuthError, NoteError...)
│   └── util/                       # Resource.kt (Success / Error / Loading)
│
├── data/                          # Implementación concreta: red, mapeo y DI
│   ├── remote/
│   │   ├── ApiService.kt           # Endpoints Retrofit (login, notes, topics, questions, dashboard)
│   │   └── mock/MockApiInterceptor.kt  # Mock alterno embebido en OkHttp (ver sección Mockoon)
│   ├── datasource/                 # Interfaz + impl por feature, delega en ApiService
│   │   └── model/                  # DTOs (NoteDto, TopicDto, LoginDto, QuestionDto, MainDto...)
│   ├── mapper/                     # DTO ↔ modelo de dominio (toDomain() / toDto())
│   ├── repository/                 # Implementación real de los contratos de domain/repository
│   ├── util/SafeApiCall.kt         # Envuelve llamadas HTTP y las traduce a Resource<T>
│   └── di/                         # Módulos Hilt: NetworkModule y RepositoryModule
│
├── ApunteIAApplication.kt         # @HiltAndroidApp — punto de arranque de Hilt
└── MainActivity.kt                # Única Activity, aloja el NavHost de Compose
```

> **Nota:** dentro de `data/datasource/` existen tres objetos (`MainProvider`, `TopicProvider`, `QuizProvider`) con datos hardcodeados que **no están referenciados en ningún lugar del código actual**. Son remanentes de una versión previa a la integración con Mockoon/Retrofit; se pueden eliminar sin afectar la app.

### Patrón por capa

**Domain — Use Case**
```kotlin
class GetNotesUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(): Resource<List<Note>> = repository.getNotes()
}
```

**Data — Repository**
```kotlin
class NoteRepositoryImpl @Inject constructor(
    private val dataSource: NoteDataSource
) : NoteRepository {
    override suspend fun getNotes(): Resource<List<Note>> = safeApiCall(
        errorMapper = { it.toNoteError().toUserMessage() }
    ) { dataSource.getNotes().toDomain() }
}
```

**Presentation — ViewModel**
```kotlin
@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()
    // ...
}
```

Todo resultado async viaja envuelto en `Resource<T>` (`Success` / `Error` / `Loading`), así la UI reacciona a errores de red sin manejar excepciones directamente.

---

## 🔌 Conexión con la API (Mockoon)

El backend está simulado con **[Mockoon](https://mockoon.com/)**. El archivo `ApunteIA Mock API.json` en la raíz del repo es un *environment* exportado de Mockoon que define un servidor en el **puerto 3001** con estas rutas:

| Método | Endpoint |
|---|---|
| POST | `auth/login` |
| GET / POST | `notes` |
| GET | `questions` |
| GET / POST | `topics` |
| GET | `dashboard/stats` |
| GET | `dashboard/activity` |

### Cómo levantarlo

1. Instalar [Mockoon Desktop](https://mockoon.com/download/).
2. Abrir Mockoon → **File → Open environment** → seleccionar `ApunteIA Mock API.json`.
3. Presionar **Start** (▶) para levantar el servidor en `http://localhost:3001`.
4. Correr la app en el **emulador de Android Studio** (no en dispositivo físico, salvo que ajustes la URL — ver más abajo).

### Cómo la app apunta a Mockoon

En `app/build.gradle.kts`:
```kotlin
buildConfigField("String", "MOCK_API_BASE_URL", "\"http://10.0.2.2:3001/\"")
buildConfigField("boolean", "USE_MOCK_API", "false")
```

- `10.0.2.2` es la IP especial que el **emulador de Android** traduce a `localhost` de la PC anfitriona. Si vas a probar en un dispositivo físico, cambia esta URL por la IP LAN real de tu computadora (ej. `http://192.168.x.x:3001/`) y asegúrate de que el dispositivo esté en la misma red.
- `NetworkModule.kt` usa este `baseUrl` para construir el `Retrofit` que inyecta `ApiService`.

### Mock alterno (offline, sin Mockoon)

Además del servidor Mockoon, el proyecto incluye un **interceptor de OkHttp** (`data/remote/mock/MockApiInterceptor.kt`) que simula las mismas 8 rutas en memoria, sin necesidad de tener Mockoon corriendo. Está **desactivado por defecto** (`USE_MOCK_API = false`). Para activarlo:

```kotlin
buildConfigField("boolean", "USE_MOCK_API", "true")
```

Con esto activo, la app nunca sale a la red: el interceptor intercepta la request y responde con datos hardcodeados (con ~250ms de latencia simulada). Además soporta forzar un error 500 agregando el header `X-Mock-Force-Error: true` a cualquier request, útil para probar pantallas de error.

Credenciales de prueba para el login (válidas tanto en Mockoon como en el interceptor):
```
email:    demo@apunteia.com
password: 123456
```

### Flujo de una petición (ejemplo: pantalla de Notas)

```
NotesScreen → NotesViewModel → GetNotesUseCase → NoteRepository (interfaz)
   → NoteRepositoryImpl → NoteDataSource → NoteDataSourceImpl
   → ApiService.getNotes() → OkHttp/Retrofit → GET http://10.0.2.2:3001/notes
   ← List<NoteDto> ← NoteMapper.toDomain() ← Resource<List<Note>> ← StateFlow → UI
```

---

## 🚀 Cómo correr el proyecto

1. Clonar el repositorio.
2. Abrirlo en **Android Studio** (Hedgehog o superior recomendado).
3. Dejar que Gradle sincronice (usa Kotlin DSL, `libs.versions.toml` como catálogo de versiones).
4. Levantar el servidor Mockoon (ver sección anterior) **o** activar `USE_MOCK_API = true` si querés trabajar sin él.
5. Correr la app en un emulador con API 24+.

---

## 🧪 Testing

Tests unitarios en `app/src/test/java/ApunteIA/FrontEnd/`:

- `data/remote/ApiServiceTest.kt` — valida el parseo de respuestas usando **MockWebServer**.
- `data/remote/mock/MockApiInterceptorTest.kt` — prueba el interceptor mock embebido.
- `data/repository/*RepositoryImplTest.kt` — prueba cada repositorio (`Note`, `Topic`, `Quiz`, `Auth`, `Main`) con datasources simulados vía Mockito, verificando el mapeo a `Resource.Success` / `Resource.Error`.

Correr todos los tests:
```
./gradlew test
```

---

## 📂 Módulos de Hilt

| Módulo | Provee |
|---|---|
| `NetworkModule` | `Gson`, `OkHttpClient` (con logging + mock interceptor condicional), `Retrofit`, `ApiService` |
| `RepositoryModule` | `@Binds` de cada interfaz de `domain/repository` y `data/datasource` a su implementación concreta |
