package no.usn.kulturminner.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import no.usn.kulturminner.data.api.GeoJsonFeatureCollection
import no.usn.kulturminner.data.api.toRoutes
import no.usn.kulturminner.data.model.Route
import java.net.URL

class RouteRepository {
    private val json = Json { ignoreUnknownKeys = true }

    private val RUTE_URL = "https://boturlag.no/DerDuGaar/Trase_wgs.geojson"

    suspend fun getRoutes(): Result<List<Route>> = runCatching {
        withContext(Dispatchers.IO) { // for at nettverksforespørselen skal skje på IO-tråden
            val rawJson = URL(RUTE_URL).readText()
            val featureCollection = json.decodeFromString<GeoJsonFeatureCollection>(rawJson)
            featureCollection.toRoutes()
        }
    }
}