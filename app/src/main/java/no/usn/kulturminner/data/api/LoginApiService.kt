package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable
import no.usn.kulturminner.data.network.RetrofitInstance
import retrofit2.http.Body
import retrofit2.http.POST

@Serializable
data class LoginRequest(val email: String, val password: String)

@Serializable
data class LoginResponse(val token: String, val userId: String)

interface LoginApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}

object LoginApi {
    val service: LoginApiService by lazy {
        RetrofitInstance.retrofit.create(LoginApiService::class.java)
    }
}