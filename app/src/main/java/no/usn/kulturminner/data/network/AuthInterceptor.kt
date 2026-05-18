package no.usn.kulturminner.data.network

import no.usn.kulturminner.data.local.TokenStorage
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenStorage: TokenStorage) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStorage.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        val response = chain.proceed(request)

        // Hvis man får 401 (utløpt token), så slett i så fall token slik at isLoggedIn() returnerer false
        if (response.code == 401) {
            tokenStorage.clear()
        }

        return response
    }
}