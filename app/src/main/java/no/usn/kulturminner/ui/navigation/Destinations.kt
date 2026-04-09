package no.usn.kulturminner.ui.navigation

sealed class Destinations(val route: String) {

    object Login : Destinations("login")
    object Explore : Destinations("explore")
    object Overview : Destinations("overview")

    object CreatePoint : Destinations("create_point")
    object EditPoint : Destinations("edit_point")
}