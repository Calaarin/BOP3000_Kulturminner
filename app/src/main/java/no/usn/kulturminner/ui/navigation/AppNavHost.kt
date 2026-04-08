package no.usn.kulturminner.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.usn.kulturminner.ui.auth.LoginScreen
import no.usn.kulturminner.ui.auth.LoginViewModel
import no.usn.kulturminner.ui.components.BottomNavBar
import no.usn.kulturminner.ui.components.TopBar
import no.usn.kulturminner.ui.createpoint.CreatePointScreen
import no.usn.kulturminner.ui.createroute.CreateRouteScreen
import no.usn.kulturminner.ui.createroute.CreateRouteViewModel
import no.usn.kulturminner.ui.editpoint.EditPointScreen
import no.usn.kulturminner.ui.editpoint.EditPointViewModel
import no.usn.kulturminner.ui.editroute.EditRouteScreen
import no.usn.kulturminner.ui.editroute.EditRouteViewModel
import no.usn.kulturminner.ui.explore.ExploreScreen
import no.usn.kulturminner.ui.explore.ExploreViewModel
import no.usn.kulturminner.ui.overview.OverviewScreen
import no.usn.kulturminner.ui.overview.OverviewViewModel
import androidx.compose.ui.Modifier
@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isLoginScreen = currentRoute == Destinations.Login.route

    Scaffold(
        topBar = {
            TopBar(navController = navController)
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
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Destinations.Login.route) {
                val viewModel: LoginViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                if (uiState.isLoginSuccess) {
                    LaunchedEffect(uiState.isLoginSuccess) {
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

            composable(Destinations.Explore.route) {
                val viewModel: ExploreViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                ExploreScreen(uiState = uiState)
            }

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
                    onSortByDate = viewModel::sortByDate
                )
            }

            composable(Destinations.CreatePoint.route) {
                CreatePointScreen()
            }

            composable(Destinations.EditPoint.route) {
                val viewModel: EditPointViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.loadPoint()
                }

                EditPointScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::onTitleChange,
                    onRadiusChange = viewModel::onRadiusChange,
                    onAudioChange = viewModel::onAudioChange,
                    onSaveClick = {
                        viewModel.updatePoint()
                        navController.popBackStack()
                    },
                    onCancelClick = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Destinations.CreateRoute.route) {
                val viewModel: CreateRouteViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                CreateRouteScreen(uiState = uiState)
            }

            composable(Destinations.EditRoute.route) {
                val viewModel: EditRouteViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.loadRoute()
                }

                EditRouteScreen(
                    uiState = uiState,
                    onNameChange = viewModel::onNameChange,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onSaveClick = {
                        viewModel.updateRoute()
                        navController.popBackStack()
                    },
                    onCancelClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}