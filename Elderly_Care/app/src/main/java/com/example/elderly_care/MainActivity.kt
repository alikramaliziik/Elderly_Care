package com.example.elderly_care

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.elderly_care.ui.theme.Elderly_CareTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Elderly_CareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var isLoggedIn by remember { mutableStateOf(false) }
                    var showProfile by remember { mutableStateOf(false) }

                    when {
                        !isLoggedIn -> {
                            LoginScreen(
                                onLoginClick = { email, password ->
                                    if (email.isNotEmpty() && password.isNotEmpty()) {
                                        isLoggedIn = true
                                        showProfile = false
                                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onSignUpClick = {
                                    Toast.makeText(this, "Sign Up coming soon", Toast.LENGTH_SHORT).show()
                                },
                                onForgotPasswordClick = {
                                    Toast.makeText(this, "Password reset coming soon", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                        showProfile -> {
                            ProfileScreen(
                                onBackClick = {
                                    showProfile = false
                                }
                            )
                        }
                        else -> {
                            DashboardScreen(
                                onLogout = {
                                    isLoggedIn = false
                                    showProfile = false
                                    Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
                                },
                                onProfileClick = {
                                    showProfile = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

