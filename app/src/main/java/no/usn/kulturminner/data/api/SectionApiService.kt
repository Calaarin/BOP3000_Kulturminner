package no.usn.kulturminner.data.api

import no.usn.kulturminner.data.network.RetrofitInstance
import retrofit2.http.*

interface SectionApiService {

    @POST("section/create")
    suspend fun createSection(
        @Body section: SectionDto
    ): Unit

    @PUT("section/update/{id}")
    suspend fun updateSection(
        @Path("id") id: String,
        @Body section: SectionDto
    ): Unit

    @DELETE("section/delete/{id}")
    suspend fun deleteSection(
        @Path("id") id: String
    ): Unit
}

object SectionApi {
    val service: SectionApiService by lazy {
        RetrofitInstance.retrofit.create(SectionApiService::class.java)
    }
}