package ApunteIA.FrontEnd.data.repository

import ApunteIA.FrontEnd.data.datasource.AuthDataSource
import ApunteIA.FrontEnd.data.mapper.toAuthError
import ApunteIA.FrontEnd.data.mapper.toDomain
import ApunteIA.FrontEnd.data.util.safeApiCall
import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.repository.AuthRepository
import ApunteIA.FrontEnd.domain.util.Resource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val dataSource: AuthDataSource
) : AuthRepository {

    override suspend fun login(email: String, password: String): Resource<AuthSession> = safeApiCall(
        errorMapper = { it.toAuthError().toUserMessage() }
    ) {
        dataSource.login(email, password).toDomain()
    }
}
