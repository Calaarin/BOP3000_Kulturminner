package no.usn.kulturminner.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val username: String,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String,
    val email: String
    // passord bevisst ekskludert
)

@Serializable
data class PasswordUpdateDto(
    val newPassword: String
)