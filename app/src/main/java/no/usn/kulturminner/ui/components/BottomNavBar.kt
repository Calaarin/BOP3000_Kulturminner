package no.usn.kulturminner.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import no.usn.kulturminner.ui.navigation.Destinations

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    ) {

        NavigationBarItem(
            selected = currentRoute == Destinations.Explore.route,
            onClick = {
                navController.navigate(Destinations.Explore.route) {
                    popUpTo(Destinations.Explore.route) { inclusive = false }
                }
            },
            icon = { Icon(Icons.Default.Map, contentDescription = "Kart") },
            label = { Text("Kart") }
        )

        NavigationBarItem(
            selected = currentRoute == Destinations.Overview.route,
            onClick = {
                navController.navigate(Destinations.Overview.route) {
                    popUpTo(Destinations.Overview.route) { inclusive = false }
                }
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Administrasjon") },
            label = { Text("Administrasjon") }
        )
    }
}