package old.people.elderly_care.ui.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import old.people.elderly_care.data.AppDao
import old.people.elderly_care.data.Caregiver

class AuthViewModel(
    private val dao: AppDao,
    application: Application
) : AndroidViewModel(application) {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun login(email: String, password: String, onResult: (Result<Caregiver>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("AuthViewModel", "Attempting login for: $email")
                
                // Validate input first
                if (email.isEmpty() || !email.contains("@")) {
                    _error.value = "Please enter a valid email address"
                    onResult(Result.failure(Exception("Invalid email")))
                    return@launch
                }

                if (password.isEmpty()) {
                    _error.value = "Please enter your password"
                    onResult(Result.failure(Exception("Empty password")))
                    return@launch
                }
                
                val caregiver = dao.getCaregiverByEmail(email.trim().lowercase())

                when {
                    caregiver == null -> {
                        Log.e("AuthViewModel", "No account found for: $email")
                        _error.value = "Account not found. Please check your email or sign up."
                        onResult(Result.failure(Exception("Account not found")))
                    }
                    caregiver.passwordHash != password -> {
                        Log.e("AuthViewModel", "Incorrect password for: $email")
                        _error.value = "Incorrect password. Please try again."
                        onResult(Result.failure(Exception("Incorrect password")))
                    }
                    else -> {
                        Log.d("AuthViewModel", "Login successful: ${caregiver.fullName}")
                        _error.value = null
                        onResult(Result.success(caregiver))
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login error", e)
                _error.value = "Login failed: ${e.message}"
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signup(
        fullName: String,
        email: String,
        password: String,
        phone: String,
        age: Int,
        gender: String,
        onResult: (Result<Unit>) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d("AuthViewModel", "Signup attempt: $email")

                // Input validation
                when {
                    fullName.isBlank() -> {
                        _error.value = "Please enter your full name"
                        onResult(Result.failure(Exception("Full name is required")))
                        return@launch
                    }
                    email.isEmpty() || !email.contains("@") -> {
                        _error.value = "Please enter a valid email address"
                        onResult(Result.failure(Exception("Invalid email")))
                        return@launch
                    }
                    phone.length < 10 -> {
                        _error.value = "Please enter a valid phone number"
                        onResult(Result.failure(Exception("Invalid phone")))
                        return@launch
                    }
                    age < 18 || age > 120 -> {
                        _error.value = "Age must be between 18 and 120"
                        onResult(Result.failure(Exception("Invalid age")))
                        return@launch
                    }
                    password.length < 8 -> {
                        _error.value = "Password must be at least 8 characters long"
                        onResult(Result.failure(Exception("Password too short")))
                        return@launch
                    }
                }

                // Check if email exists
                val existingCaregiver = dao.checkEmailExists(email.trim().lowercase())
                if (existingCaregiver != null) {
                    Log.e("AuthViewModel", "Email already exists: $email")
                    _error.value = "This email is already registered. Please login instead."
                    onResult(Result.failure(Exception("Email already exists")))
                    return@launch
                }

                // Create and insert new caregiver
                val newCaregiver = Caregiver(
                    fullName = fullName.trim(),
                    email = email.trim().lowercase(),
                    passwordHash = password, // In production, use proper password hashing!
                    phone = phone.trim(),
                    age = age,
                    gender = gender
                )
                dao.insertCaregiver(newCaregiver)
                Log.d("AuthViewModel", "Signup successful for: $email")
                _error.value = null
                onResult(Result.success(Unit))

            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup error", e)
                _error.value = "Signup failed: ${e.message}"
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
