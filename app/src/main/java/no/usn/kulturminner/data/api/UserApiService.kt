package no.usn.kulturminner.data.api

import no.usn.kulturminner.data.network.RetrofitInstance
import retrofit2.http.*

interface UserApiService {

    // Hent en enkelt bruker (uten passord) TODO: endepunkter må matches med server

    @GET("user/{id}")
    suspend fun getUser(@Path("id") id: String): UserDto

    // Oppdater bruker (PATCH anbefalt for delvis oppdatering - TODO; se mer på hva som er anbefalt)
    @PATCH("user/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body user: UserDto
    ): UserDto

    // Alternativ full oppdatering (PUT)
    @PUT("user/{id}")
    suspend fun updateUserFull(
        @Path("id") id: String,
        @Body user: UserDto
    ): UserDto

    // Endre passord – kun nytt passord sendes
    @PATCH("user/{id}/password")
    suspend fun updatePassword(
        @Path("id") id: String,
        @Body request: PasswordUpdateDto
    )

}

// Lazy-instans
object UserApi {
    val service: UserApiService by lazy {
        RetrofitInstance.retrofit.create(UserApiService::class.java)
    }
}