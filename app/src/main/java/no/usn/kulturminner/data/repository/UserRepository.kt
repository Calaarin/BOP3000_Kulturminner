package no.usn.kulturminner.data.repository

import no.usn.kulturminner.data.api.UserDto
import no.usn.kulturminner.data.model.User
import no.usn.kulturminner.data.source.UserSource

interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun updatePassword(id: String, newPassword: String): Result<Unit>
}

class UserRepositoryImpl(
    private val remoteSource: UserSource
) : UserRepository {

    override suspend fun getUser(id: String): Result<User> = runCatching {
        remoteSource.getUser(id).toModel()
    }

    override suspend fun updateUser(user: User): Result<User> = runCatching {
        val dto = user.toDto()
        remoteSource.updateUser(user.id, dto).toModel()
    }

    override suspend fun updatePassword(id: String, newPassword: String): Result<Unit> = runCatching {
        remoteSource.updatePassword(id, newPassword)
    }
}

// ==================== Mapper ====================

private fun UserDto.toModel() = User(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email
)

private fun User.toDto() = UserDto(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
    email = email
)