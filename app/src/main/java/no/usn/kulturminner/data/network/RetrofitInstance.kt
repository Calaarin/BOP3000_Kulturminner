package no.usn.kulturminner.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import no.usn.kulturminner.BuildConfig
import no.usn.kulturminner.data.local.TokenStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // URL-ene er satt opp til å kunne byttes mellom localhost og hostet server

    private const val LOCALHOST_URL = "http://10.0.2.2:3000/"
    private const val HOSTED_SERVER_URL = "https://derdugarbackend.onrender.com/"
    private const val BASE_URL = LOCALHOST_URL

    // JSON-konfigurasjon tilpasset Kotlin og @Serializable
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    private lateinit var tokenStorage: TokenStorage

    fun initialize(tokenStorage: TokenStorage) {
        this.tokenStorage = tokenStorage
    }

    // Hoved Retrofit-instansen som brukes overalt
    val retrofit: Retrofit by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        // OkHttp-klient med logging (kun i debug)
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStorage))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }
}