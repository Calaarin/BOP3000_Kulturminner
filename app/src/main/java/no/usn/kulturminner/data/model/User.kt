package no.usn.kulturminner.data.model

// En bruker. Kan ha ingen eller mange punkter
data class User(
    val id: String,
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val email: String
)
