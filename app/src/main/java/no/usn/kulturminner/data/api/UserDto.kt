package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val email: String
    // passord bevisst ekskludert
)

@Serializable
data class PasswordUpdateDto(
    val newPassword: String
)