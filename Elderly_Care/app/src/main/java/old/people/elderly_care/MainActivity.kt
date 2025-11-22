package old.people.elderly_care

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.Caregiver
import old.people.elderly_care.ui.about.AboutScreen
import old.people.elderly_care.ui.add.AddActivityScreen
import old.people.elderly_care.ui.auth.AuthViewModel
import old.people.elderly_care.ui.auth.LoginScreen
import old.people.elderly_care.ui.auth.SignUpScreen
import old.people.elderly_care.ui.profile.ProfileScreen
import old.people.elderly_care.ui.theme.Elderly_CareTheme
import old.people.elderly_care.ui.aboutme.AboutMeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Elderly_CareTheme {       
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val authViewModel = viewModel<AuthViewModel>(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(
                    AppDatabase.getInstance(context).dao(),
                    context.applicationContext as android.app.Application
                ) as T
            }
        }
    )

    var loggedInCaregiver by remember { mutableStateOf<Caregiver?>(null) }
    val error by authViewModel.error.collectAsState()

    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            authViewModel.clearError()
        }
    }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { caregiver ->
                    loggedInCaregiver = caregiver
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpClick = { navController.navigate("signup") },
                onForgotPasswordClick = {
                    Toast.makeText(context, "Password reset coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable("signup") {
            SignUpScreen(
                viewModel = authViewModel,
                onSignUpSuccess = {
                    Toast.makeText(context, "Account created! Please login", Toast.LENGTH_SHORT).show()
                    navController.navigate("login") { popUpTo("signup") { inclusive = true } }
                },
                onBackClick = { navController.popBackStack() },
                onLoginClick = { navController.navigate("login") }
            )
        }

        composable("dashboard") {
            val caregiverId = loggedInCaregiver?.id
            if (caregiverId != null) {
                DashboardScreen(
                    navController = navController,
                    caregiverId = caregiverId,
                    onLogout = {
                        loggedInCaregiver = null
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                )
            } else {
                LaunchedEffect(Unit) {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            }
        }

        composable("add_activity") {
            val caregiverId = loggedInCaregiver?.id ?: return@composable
            AddActivityScreen(navController = navController, caregiverId = caregiverId)
        }

        composable("profile") {
            loggedInCaregiver?.let {
                ProfileScreen(caregiver = it, onBackClick = { navController.popBackStack() })
            } ?: LaunchedEffect(Unit) { navController.navigate("login") { popUpTo(0) } }
        }

        composable("about") {
            AboutScreen(navController = navController)
        }

        composable("about_me") {
            loggedInCaregiver?.let { caregiver ->
                AboutMeScreen(
                    caregiver = caregiver,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = {
                        Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                )
            } ?: LaunchedEffect(Unit) { navController.navigate("login") { popUpTo(0) } }
        }
    }
}
