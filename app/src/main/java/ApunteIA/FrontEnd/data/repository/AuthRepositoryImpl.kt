package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.domain.error.AuthError
import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthSession {
        return try {
            dataSource.login(email, password).toDomain()
        } catch (e: IOException) {
            throw AuthError.NoConnection
        } catch (e: HttpException) {
            when (e.code()) {
                401, 403 -> throw AuthError.InvalidCredentials
                else -> throw AuthError.ServerError(e.code())
            }
        } catch (e: Exception) {
            throw AuthError.Unknown
        }
    }
}