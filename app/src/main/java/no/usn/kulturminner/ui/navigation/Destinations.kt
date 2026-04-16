package no.usn.kulturminner.ui.navigation

sealed class Destinations(val route: String) {

    object Login : Destinations("login")
    object Explore : Destinations("explore")
    object Overview : Destinations("overview")
    object CreatePoint : Destinations("create_point")

    // EditPoint bruker data object siden den trenger en parameter og createRoute funksjon
    data object EditPoint : Destinations("edit_point/{pointId}") {
        fun createRoute(pointId: String): String {
            return "edit_point/$pointId"
        }
    }
}