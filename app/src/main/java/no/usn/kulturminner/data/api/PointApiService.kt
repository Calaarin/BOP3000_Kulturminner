package no.usn.kulturminner.data.api

import kotlinx.serialization.Contextual
import no.usn.kulturminner.data.network.RetrofitInstance
import retrofit2.http.*

interface PointApiService {

    // === Henting === TODO: endepunkter må matches med server

    // Hent ALLE punkter (til kart - vi får se om mer optimalisert datafetching bør gjøres etter hvert)
    @GET("point")
    suspend fun getAllPoints(): List<PointDto>

    // Hent alle punkter for innlogget bruker (nåværende løsning med dummy-id)
    @GET("point/search")
    suspend fun getMyPoints(
        @Query("user_id") userId: String
    ): List<PointDto>

    // Fremtidig versjon når auth er på plass (dette er bedre praksis med bruk av auth token)
    // @GET("points/me")
    // suspend fun getMyPoints(): List<PointDto>

    // Hent ett enkelt punkt
    @GET("point/{id}")
    suspend fun getPoint(
        @Path("id") id: String
    ): PointDto

    // === Oppretting / oppdatering / sletting ===

    // Opprett nytt punkt
    @POST("point")
    suspend fun createPoint(
        @Body point: PointDto
    ): PointDto

    // Oppdater punkt - PATCH er anbefalt (delvis oppdatering)

    /* Ny versjon
    @PATCH("point/update/{id}")
    suspend fun updatePoint(
        @Path("id") id: String,
        @Body fields: Map<String, @Contextual Any> // lar oss sende bare akkurat de feltene vi vil oppdatere
    ): PointDto

     */

    /* Nyere versjon med bruk av PatchPointDto
    @PATCH("point/update/{id}")
    suspend fun patchPoint(
        @Path("id") id: String,
        @Body fields: PointPatchDto
    ): PointDto
     */

    // Gammel versjon som antagelig må byttes ut
    @PATCH("point/{id}")
    suspend fun updatePoint(
        @Path("id") id: String,
        @Body point: PointDto
    ): PointDto

    // Alternativ full oppdatering (PUT) - vi beholder denne til foreløpig bruk
    @PUT("point/{id}")
    suspend fun updatePointFull(
        @Path("id") id: String,
        @Body point: PointDto
    ): PointDto

    // Slett punkt
    @DELETE("point/{id}")
    suspend fun deletePoint(
        @Path("id") id: String)
}

// Lazy-instans som brukes fra Source
object PointApi {
    val service: PointApiService by lazy {
        RetrofitInstance.retrofit.create(PointApiService::class.java)
    }
}