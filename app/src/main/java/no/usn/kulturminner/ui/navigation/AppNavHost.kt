package no.usn.kulturminner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.currentBackStackEntryAsState

import no.usn.kulturminner.ui.auth.LoginScreen
import no.usn.kulturminner.ui.auth.LoginViewModel
import no.usn.kulturminner.ui.explore.ExploreScreen
import no.usn.kulturminner.ui.explore.ExploreViewModel
import no.usn.kulturminner.ui.overview.OverviewScreen
import no.usn.kulturminner.ui.overview.OverviewViewModel
import no.usn.kulturminner.ui.createpoint.CreatePointScreen
import no.usn.kulturminner.ui.createpoint.CreatePointViewModel
import no.usn.kulturminner.ui.editpoint.EditPointScreen
import no.usn.kulturminner.ui.editpoint.EditPointViewModel
import no.usn.kulturminner.ui.createroute.CreateRouteScreen
import no.usn.kulturminner.ui.createroute.CreateRouteViewModel
import no.usn.kulturminner.ui.editroute.EditRouteScreen
import no.usn.kulturminner.ui.editroute.EditRouteViewModel
import no.usn.kulturminner.ui.mediapanel.MediaPanelScreen
import no.usn.kulturminner.ui.mediapanel.MediaPanelViewModel
import no.usn.kulturminner.ui.mediapanel.AddMediaTextScreen
import no.usn.kulturminner.ui.mediapanel.TextSection
import androidx.compose.material3.Text
import no.usn.kulturminner.ui.components.TopBar
import no.usn.kulturminner.ui.components.BottomNavBar


@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val isLoginScreen = currentRoute == Destinations.Login.route

    Scaffold(
        topBar = {
            TopBar(
                navController = navController
            )
        },
        bottomBar = {
            if (!isLoginScreen) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Destinations.Login.route,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {

            // --- LOGIN ---
            composable(Destinations.Login.route) {
                val viewModel: LoginViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                if (uiState.isLoginSuccess) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Destinations.Overview.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    }
                }

                LoginScreen(
                    uiState = uiState,
                    onEmailChange = viewModel::onEmailChange,
                    onPasswordChange = viewModel::onPasswordChange,
                    onLoginClick = viewModel::login,
                    onSkipClick = {
                        navController.navigate(Destinations.Overview.route) {
                            popUpTo(Destinations.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // --- EXPLORE ---
            composable(Destinations.Explore.route) {
                val viewModel: ExploreViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                ExploreScreen(uiState = uiState)
            }

            // --- OVERVIEW (admin dashboard) ---
            composable(Destinations.Overview.route) {
                val viewModel: OverviewViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                OverviewScreen(
                    uiState = uiState,
                    onCreatePointClick = {
                        navController.navigate(Destinations.CreatePoint.route)
                    },
                    onEditPointClick = {
                        navController.navigate(Destinations.EditPoint.route)
                    },
                    onEditRouteClick = {
                        navController.navigate(Destinations.EditRoute.route)
                    },
                    onSortAlphabetically = viewModel::sortAlphabetically,
                    onSortByDate = viewModel::sortByDate,
                    onMediaPanelClick = {
                        navController.navigate(Destinations.MediaPanel.route)
                    },
                    // Knytter "Legg til tekst"-knappen til den nye ruta:
                    onAddMediaTextClick = {
                        navController.navigate(Destinations.AddMediaText.route)
                    }
                )
            }

            // --- CREATE POINT ---
            composable(Destinations.CreatePoint.route) {
                val viewModel: CreatePointViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                CreatePointScreen()
            }

            // --- EDIT POINT ---
            composable(Destinations.EditPoint.route) {
                val viewModel: EditPointViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                EditPointScreen(uiState = uiState)
            }

            // --- CREATE ROUTE ---
            composable(Destinations.CreateRoute.route) {
                val viewModel: CreateRouteViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                CreateRouteScreen(uiState = uiState)
            }

            // --- EDIT ROUTE ---
            composable(Destinations.EditRoute.route) {
                val viewModel: EditRouteViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                EditRouteScreen(uiState = uiState)
            }

            // --- MEDIA PANEL (Visning) ---
            composable(Destinations.MediaPanel.route) {
                val viewModel: MediaPanelViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                MediaPanelScreen(
                    uiState = uiState
                )
            }

            // --- ADD MEDIA TEXT (Siden hvor man skriver inn tekst og velger kolonner) ---
            // ... (din siste composable-blokk her) ...
            composable(Destinations.AddMediaText.route) {
                AddMediaTextScreen(
                    onSaveClick = { newSection ->
                        navController.popBackStack()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}