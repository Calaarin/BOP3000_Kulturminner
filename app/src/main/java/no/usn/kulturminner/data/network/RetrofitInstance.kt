package no.usn.kulturminner.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import no.usn.kulturminner.BuildConfig
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    // TODO: Endre til riktig base URL for vår server
    private const val BASE_URL = "https://vår-server-url.com/api/"

    // JSON-konfigurasjon tilpasset Kotlin og @Serializable
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    // OkHttp-klient med logging (kun i debug)
    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY      // Logg hele request + response
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Hoved Retrofit-instansen som brukes overalt
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
    }

    // TODO: Fremtidig auth-interceptor (når login er på plass)
    // private val authInterceptor = Interceptor { chain ->
    //     val request = chain.request().newBuilder()
    //         .addHeader("Authorization", "Bearer ${AuthManager.token}")
    //         .build()
    //     chain.proceed(request)
    // }
}