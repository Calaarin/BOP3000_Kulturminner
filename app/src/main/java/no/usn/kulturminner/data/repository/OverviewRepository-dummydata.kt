package no.usn.kulturminner.data.repository

class `OverviewRepository-dummydata` {

    suspend fun getDemoPoints(): Result<List<String>> {
        return try {
            // Simulerer backend-kall
            val points = listOf(
                "Kulturstien",
                "Bø Prestegård",
                "Gullbring"
            )
            Result.success(points)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
