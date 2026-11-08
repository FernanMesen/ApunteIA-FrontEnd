package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.UserDto

interface AuthDataSource {
    suspend fun login(email: String, password: String): LoginResponseDto
    suspend fun register(fullName: String, email: String, password: String): UserDto
    suspend fun ping()
    suspend fun deleteAccount()
}