package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.AuthSession
import ApunteIA.FrontEnd.domain.util.Resource

interface AuthRepository {
    suspend fun login(email: String, password: String): Resource<AuthSession>
}
