package no.usn.kulturminner.data.repository

import no.usn.kulturminner.data.api.UserDto
import no.usn.kulturminner.data.model.User
import no.usn.kulturminner.data.source.UserSource

interface UserRepository {
    suspend fun getUser(id: String): Result<User>
    suspend fun updateUser(user: User): Result<User>
    suspend fun updatePassword(id: String, newPassword: String): Result<Unit>
    suspend fun getDummyUser(id: String): Result<User>
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

    // ==================== Dummy data for testing ====================

    override suspend fun getDummyUser(id: String): Result<User> = runCatching {
        User(
            id = "u1",
            username = "arne_h",
            firstName = "Arne William",
            lastName = "Hjeltnes",
            email = "awhjeltnes@outlook.com"
        )
    }
}

// ==================== Mapper ====================

// (Disse må oppdateres etter behov for hva som skal sendes før de er brukbare)

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