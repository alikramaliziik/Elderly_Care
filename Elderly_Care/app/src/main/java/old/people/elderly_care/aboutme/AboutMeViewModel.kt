package old.people.elderly_care.ui.aboutme

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.Caregiver
import java.io.ByteArrayOutputStream

class AboutMeViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).dao()

    private val _caregiver = MutableStateFlow<Caregiver?>(null)
    val caregiver: StateFlow<Caregiver?> = _caregiver

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _profileImageUri = MutableStateFlow<Uri?>(null)
    val profileImageUri: StateFlow<Uri?> = _profileImageUri

    private val _profileImageBitmap = MutableStateFlow<Bitmap?>(null)
    val profileImageBitmap: StateFlow<Bitmap?> = _profileImageBitmap

    private val _showImageSourceDialog = MutableStateFlow(false)
    val showImageSourceDialog: StateFlow<Boolean> = _showImageSourceDialog

    fun setCaregiver(caregiver: Caregiver) {
        _caregiver.value = caregiver
    }

    fun updateCaregiverProfile(
        caregiverId: Int,
        fullName: String,
        email: String,
        phone: String,
        age: Int,
        gender: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val current = _caregiver.value ?: return@launch

                val bitmapBytes = _profileImageBitmap.value?.let { bitmap ->
                    ByteArrayOutputStream().use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        stream.toByteArray()
                    }
                }

                val updatedCaregiver = current.copy(
                    fullName = fullName,
                    email = email,
                    phone = phone,
                    age = age,
                    gender = gender,
                    profilePicture = bitmapBytes ?: current.profilePicture  // keep old photo if no new one
                )

                // Use insert with REPLACE strategy to update the caregiver
                dao.insertCaregiver(updatedCaregiver)

                _caregiver.value = updatedCaregiver
                _profileImageUri.value = null
                _profileImageBitmap.value = null
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Update failed: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
        // Clear bitmap when URI is set
        _profileImageBitmap.value = null
    }

    fun setProfileImageBitmap(bitmap: Bitmap) {
        _profileImageBitmap.value = bitmap
        // Clear URI when bitmap is set
        _profileImageUri.value = null
    }

    fun showImageSourceDialog() {
        _showImageSourceDialog.value = true
    }

    fun hideImageSourceDialog() {
        _showImageSourceDialog.value = false
    }

    fun clearProfileImage() {
        _profileImageUri.value = null
        _profileImageBitmap.value = null
        // Also clear from caregiver data in state
        _caregiver.value = _caregiver.value?.copy(profilePicture = null)
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
