package ApunteIA.FrontEnd.data.mapper

import ApunteIA.FrontEnd.data.datasource.model.LoginResponseDto
import ApunteIA.FrontEnd.domain.model.AuthSession

fun LoginResponseDto.toDomain(): AuthSession = AuthSession(
    token = token,
    userName = userName
)