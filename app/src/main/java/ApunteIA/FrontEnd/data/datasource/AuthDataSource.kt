package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto

interface AuthDataSource {
    suspend fun login(email: String, password: String): LoginResponseDto
}