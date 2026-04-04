package no.usn.kulturminner.data.source

import no.usn.kulturminner.data.api.PasswordUpdateDto
import no.usn.kulturminner.data.api.UserApi
import no.usn.kulturminner.data.api.UserDto

class UserSource {

    suspend fun getUser(id: String): UserDto =
        UserApi.service.getUser(id)

    suspend fun updateUser(id: String, userDto: UserDto): UserDto =
        UserApi.service.updateUser(id, userDto)

    suspend fun updateUserFull(id: String, userDto: UserDto): UserDto =
        UserApi.service.updateUserFull(id, userDto)

    suspend fun updatePassword(id: String, newPassword: String) =
        UserApi.service.updatePassword(id, PasswordUpdateDto(newPassword))
}