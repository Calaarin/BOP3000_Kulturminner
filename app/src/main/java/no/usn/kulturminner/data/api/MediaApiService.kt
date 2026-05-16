package no.usn.kulturminner.data.api

import kotlinx.serialization.Serializable
import no.usn.kulturminner.data.network.RetrofitInstance
import okhttp3.MultipartBody
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Body

@Serializable
data class MediaUploadResponse(val url: String)

interface MediaApiService {
    @Multipart
    @POST("media/upload/{type}")
    suspend fun uploadMedia(
        @Path("type") type: String,
        @Part file: MultipartBody.Part
    ): MediaUploadResponse

    @HTTP(method = "DELETE", path = "media/delete", hasBody = true)
    suspend fun deleteMedia(
        @Body body: DeleteMediaRequest
    ): Unit
}

@Serializable
data class DeleteMediaRequest(val url: String)

object MediaApi {
    val service: MediaApiService by lazy {
        RetrofitInstance.retrofit.create(MediaApiService::class.java)
    }
}