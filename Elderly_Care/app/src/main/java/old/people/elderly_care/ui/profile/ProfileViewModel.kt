package old.people.elderly_care.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import old.people.elderly_care.data.AppDatabase
import old.people.elderly_care.data.Elderly

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).dao()

    private val _elderlyList = MutableStateFlow<List<Elderly>>(emptyList())
    val elderlyList: StateFlow<List<Elderly>> = _elderlyList

    private val _selectedElderlyActivities = MutableStateFlow<List<old.people.elderly_care.data.ActivityEntity>>(emptyList())
    val selectedElderlyActivities: StateFlow<List<old.people.elderly_care.data.ActivityEntity>> = _selectedElderlyActivities

    private val _showElderlyDialog = MutableStateFlow(false)
    val showElderlyDialog: StateFlow<Boolean> = _showElderlyDialog

    private val _showEditElderlyDialog = MutableStateFlow(false)
    val showEditElderlyDialog: StateFlow<Boolean> = _showEditElderlyDialog

    private val _selectedElderly = MutableStateFlow<Elderly?>(null)
    val selectedElderly: StateFlow<Elderly?> = _selectedElderly

    private val _elderlyToEdit = MutableStateFlow<Elderly?>(null)
    val elderlyToEdit: StateFlow<Elderly?> = _elderlyToEdit

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog

    private val _elderlyToDelete = MutableStateFlow<Elderly?>(null)
    val elderlyToDelete: StateFlow<Elderly?> = _elderlyToDelete

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadElderly(caregiverId: Int) {
        viewModelScope.launch {
            try {
                val list = dao.getElderlyByCaregiver(caregiverId)
                _elderlyList.value = list
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load elderly list: ${e.message}"
            }
        }
    }

    fun showAddElderlyDialog() {
        _showElderlyDialog.value = true
    }

    fun hideAddElderlyDialog() {
        _showElderlyDialog.value = false
    }

    fun showEditElderlyDialog(elderly: Elderly) {
        _elderlyToEdit.value = elderly
        _showEditElderlyDialog.value = true
    }

    fun hideEditElderlyDialog() {
        _showEditElderlyDialog.value = false
        _elderlyToEdit.value = null
    }

    fun showDeleteElderlyDialog(elderly: Elderly) {
        _elderlyToDelete.value = elderly
        _showDeleteDialog.value = true
    }

    fun hideDeleteElderlyDialog() {
        _showDeleteDialog.value = false
        _elderlyToDelete.value = null
    }

    fun selectElderly(elderly: Elderly) {
        _selectedElderly.value = elderly
        viewModelScope.launch {
            try {
                val today = java.time.LocalDate.now().toString()
                val activities = dao.getActivitiesForElderlyOnDate(elderly.id, today)
                _selectedElderlyActivities.value = activities
                _errorMessage.value = null
            } catch (e: Exception) {
                _selectedElderlyActivities.value = emptyList()
                _errorMessage.value = "Failed to load activities: ${e.message}"
            }
        }
    }

    fun addElderly(
        caregiverId: Int,
        fullName: String,
        age: String,
        gender: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val elderly = Elderly(
                    caregiverId = caregiverId,
                    fullName = fullName,
                    age = age.toInt(),
                    gender = gender,
                    phone = if (phone.isBlank()) null else phone
                )
                dao.insertElderly(elderly)
                loadElderly(caregiverId)
                hideAddElderlyDialog()
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add elderly: ${e.message}"
            }
        }
    }

    fun updateElderly(
        elderlyId: Int,
        fullName: String,
        age: String,
        gender: String,
        phone: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val currentElderly = _elderlyToEdit.value
                if (currentElderly != null) {
                    val updatedElderly = currentElderly.copy(
                        fullName = fullName,
                        age = age.toInt(),
                        gender = gender,
                        phone = if (phone.isBlank()) null else phone
                    )
                    dao.insertElderly(updatedElderly)
                    loadElderly(currentElderly.caregiverId)
                    
                    // Update selected elderly if it's the same one
                    if (_selectedElderly.value?.id == elderlyId) {
                        _selectedElderly.value = updatedElderly
                    }
                    
                    hideEditElderlyDialog()
                    _errorMessage.value = null
                    onSuccess()
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update elderly: ${e.message}"
            }
        }
    }

    fun deleteElderly(caregiverId: Int) {
        viewModelScope.launch {
            try {
                val elderlyToDelete = _elderlyToDelete.value
                if (elderlyToDelete != null) {
                    // Use transaction to delete elderly and all their activities
                    dao.deleteElderlyWithActivities(elderlyToDelete.id)
                    
                    // Reload the elderly list
                    loadElderly(caregiverId)
                    
                    // Clear selection if the deleted elderly was selected
                    if (_selectedElderly.value?.id == elderlyToDelete.id) {
                        _selectedElderly.value = null
                        _selectedElderlyActivities.value = emptyList()
                    }
                    
                    hideDeleteElderlyDialog()
                    _errorMessage.value = null
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete elderly: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    // Get statistics for elderly
    suspend fun getElderlyStatistics(elderlyId: Int): Pair<Int, Int> {
        return try {
            val totalActivities = dao.getActivityCountForElderly(elderlyId)
            val completedActivities = dao.getCompletedActivityCountForElderly(elderlyId)
            totalActivities to completedActivities
        } catch (e: Exception) {
            0 to 0
        }
    }
}
