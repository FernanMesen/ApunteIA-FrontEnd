package ApunteIA.FrontEnd.data.datasource

import ApunteIA.FrontEnd.data.datasource.model.LoginRequestDto
import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.data.datasource.model.RegisterRequestDto
import ApunteIA.FrontEnd.data.datasource.model.UserDto
import ApunteIA.FrontEnd.data.remote.ApiService
import javax.inject.Inject

class AuthDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : AuthDataSource {
    override suspend fun login(email: String, password: String): LoginResponseDto =
        apiService.login(LoginRequestDto(email = email, password = password))

    override suspend fun register(fullName: String, email: String, password: String): UserDto =
        apiService.register(RegisterRequestDto(fullName = fullName, email = email, password = password))

    override suspend fun ping() {
        apiService.ping().body()?.close()
    }

    override suspend fun deleteAccount() {
        apiService.deleteAccount()
    }
}