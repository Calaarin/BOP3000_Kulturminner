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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.android.gms.location.FusedLocationProviderClient

import no.usn.kulturminner.data.repository.LocationRepository
import no.usn.kulturminner.data.repository.PointRepositoryImpl
import no.usn.kulturminner.data.repository.RouteRepository
import no.usn.kulturminner.data.repository.SectionRepository
import no.usn.kulturminner.data.repository.SectionRepositoryImpl
import no.usn.kulturminner.data.repository.UserRepositoryImpl
import no.usn.kulturminner.data.source.PointSource
import no.usn.kulturminner.data.source.UserSource
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
import no.usn.kulturminner.ui.components.TopBar
import no.usn.kulturminner.ui.components.BottomNavBar
import no.usn.kulturminner.ui.createpoint.CreatePointViewModelFactory
import no.usn.kulturminner.ui.editpoint.EditPointViewModelFactory
import no.usn.kulturminner.ui.explore.ExploreViewModelFactory
import no.usn.kulturminner.ui.overview.OverviewViewModelFactory
import no.usn.kulturminner.ui.overview.SortType

@Composable
fun AppNavHost(fusedLocationClient: FusedLocationProviderClient) {

    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val isLoginScreen = currentRoute == Destinations.Login.route

    // Oppretter repositorier til ViewModelFactories
    // (Vi kunne brukt dependency-injection-biblioteket "Hilt", men foretrekte å ikke abstrahere instansiering i dette prosjektet)
    val locationRepo = remember { LocationRepository(fusedLocationClient) }
    val sectionRepo = remember { SectionRepositoryImpl() }
    val pointRepo= remember { PointRepositoryImpl(
        PointSource(),
        sectionRepository = sectionRepo
    ) }
    val userRepo = remember { UserRepositoryImpl(UserSource()) }
    val routeRepo = remember { RouteRepository() }

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

                // Når innlogging lykkes i ViewModel, navigerer vi til Overview
                if (uiState.isLoginSuccess) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Destinations.Overview.route) {
                            // Fjerner login fra historikken så man ikke kan gå "tilbake" til den
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
                val viewModel: ExploreViewModel = viewModel(
                    factory = ExploreViewModelFactory(pointRepo, locationRepo, routeRepo)   // routeRepo skal legges til etter hvert
                )
                val uiState by viewModel.uiState.collectAsState()

                ExploreScreen(
                    uiState = uiState,
                    onToggleSimulationPause = viewModel::toggleSimulationPause,
                    onIncreaseSpeed = viewModel::increaseSpeed,
                    onDecreaseSpeed = viewModel::decreaseSpeed,
                    onToggleLocationMode = viewModel::toggleLocationMode
                )
            }

            // --- OVERVIEW (admin dashboard) ---
            composable(Destinations.Overview.route) {
                val viewModel: OverviewViewModel = viewModel(
                    factory = OverviewViewModelFactory(userRepo, pointRepo)
                )
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(Unit) {
                    viewModel.fetchMyPoints()
                }

                OverviewScreen(
                    uiState = uiState,
                    onCreatePointClick = {
                        navController.navigate(Destinations.CreatePoint.route)
                    },
                    onEditPointClick = { pointId: String ->
                        navController.navigate(Destinations.EditPoint.createRoute(pointId))
                    },
                    onDeletePointClick = { pointId -> viewModel.requestDeletePoint(pointId) },
                    onConfirmDelete = viewModel::confirmDeletePoint,
                    onCancelDelete = viewModel::cancelDelete,
                    onSortAlphabetically = { viewModel.changeSortType(SortType.ALPHABETICAL) },
                    onSortByDate = { viewModel.changeSortType(SortType.DATE) }
                )
            }

            /// --- CREATE POINT ---
            composable(Destinations.CreatePoint.route) {
                val viewModel: CreatePointViewModel = viewModel(
                    factory = CreatePointViewModelFactory(pointRepo, context)
                )
                val uiState by viewModel.uiState.collectAsState()

                // Naviger tilbake når punkt er lagret
                LaunchedEffect(uiState.isSuccess) {
                    if (uiState.isSuccess) {
                        navController.popBackStack()
                    }
                }

                CreatePointScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onLatChange = viewModel::updateLat,
                    onLngChange = viewModel::updateLng,
                    onRadiusChange = viewModel::updateRadius,
                    onAudioUrlChange = viewModel::updateAudioUrl,
                    onUpdateSection = viewModel::updateSection,
                    onExpandSectionCountDropdown = viewModel::expandSectionCountDropdown,
                    onDismissSectionCountDropdown = viewModel::dismissSectionCountDropdown,
                    onSectionCountChange = viewModel::setSectionCount,
                    onImageSelected = viewModel::selectImage,
                    onVideoSelected = viewModel::selectVideo,
                    onAudioSelected = viewModel::selectAudio,
                    onRemoveImage = viewModel::removeImage,
                    onRemoveVideo = viewModel::removeVideo,
                    onRemoveAudio = viewModel::removeAudio,
                    onSaveClick = viewModel::createPoint,
                    onCancelClick = { navController.popBackStack() },
                    onDismissPopup = viewModel::dismissPopup
                )
            }

            // --- EDIT POINT ---
            composable(
                route = Destinations.EditPoint.route,
                arguments = listOf(
                    navArgument("pointId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val pointId = backStackEntry.arguments?.getString("pointId") ?: ""

                val viewModel: EditPointViewModel = viewModel(
                    factory = EditPointViewModelFactory(pointRepo, context)
                )

                // Last punktet når vi navigerer hit
                LaunchedEffect(pointId) {
                    if (pointId.isNotBlank()) {
                        viewModel.loadPoint(pointId)
                    }
                }

                val uiState by viewModel.uiState.collectAsState()

                // Naviger tilbake når punkt er lagret
                LaunchedEffect(uiState.isSuccess) {
                    if (uiState.isSuccess) {
                        navController.popBackStack()
                    }
                }

                EditPointScreen(
                    uiState = uiState,
                    onTitleChange = viewModel::updateTitle,
                    onLatChange = viewModel::updateLat,
                    onLngChange = viewModel::updateLng,
                    onRadiusChange = viewModel::updateRadius,
                    onAudioUrlChange = viewModel::updateAudioUrl,
                    onUpdateSection = viewModel::updateSection,
                    onExpandSectionCountDropdown = viewModel::expandSectionCountDropdown,
                    onDismissSectionCountDropdown = viewModel::dismissSectionCountDropdown,
                    onSectionCountChange = viewModel::setSectionCount,
                    onImageSelected = viewModel::selectImage,
                    onVideoSelected = viewModel::selectVideo,
                    onAudioSelected = viewModel::selectAudio,
                    onRemoveImage = viewModel::removeImage,
                    onRemoveVideo = viewModel::removeVideo,
                    onRemoveAudio = viewModel::removeAudio,
                    onSaveClick = viewModel::updatePoint,
                    onCancelClick = { navController.popBackStack() },
                    onDismissPopup = viewModel::dismissPopup
                )
            }
        }
    }
}