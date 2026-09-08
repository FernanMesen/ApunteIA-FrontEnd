package ApunteIA.FrontEnd.domain.repository

import ApunteIA.FrontEnd.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthSession
}