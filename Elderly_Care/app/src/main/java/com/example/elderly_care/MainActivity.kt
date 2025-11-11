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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.Caregiver
import old.people.elderly_care.ui.LoginScreen
import old.people.elderly_care.ui.SignUpScreen
import old.people.elderly_care.ui.add.AddActivityScreen
import old.people.elderly_care.ui.profile.ProfileScreen
import old.people.elderly_care.ui.theme.Elderly_CareTheme
import old.people.elderly_care.ui.auth.AuthViewModel

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
    
    val authViewModel = androidx.lifecycle.viewmodel.compose.viewModel<AuthViewModel>(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
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
        error?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
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
                onSignUpClick = {
                    navController.navigate("signup")
                },
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
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onLoginClick = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        
        composable("dashboard") {
            DashboardScreen(
                navController = navController,
                onLogout = {
                    loggedInCaregiver = null
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        
        composable("profile") {
            loggedInCaregiver?.let { caregiver ->
                ProfileScreen(
                    caregiver = caregiver,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable("add_activity") {
            AddActivityScreen(navController = navController)
        }
    }
}
