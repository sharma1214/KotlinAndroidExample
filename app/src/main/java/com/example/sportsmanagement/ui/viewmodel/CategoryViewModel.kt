package com.example.sportsmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsmanagement.data.model.SportCategory
import com.example.sportsmanagement.data.model.User
import com.example.sportsmanagement.data.repository.SportRepository
import com.example.sportsmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val sportRepository = SportRepository()
    private val userRepository = UserRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _selectedCategory = MutableLiveData<SportCategory?>()
    val selectedCategory: LiveData<SportCategory?> = _selectedCategory

    private val _categoryParticipants = MutableLiveData<List<User>>()
    val categoryParticipants: LiveData<List<User>> = _categoryParticipants

    val categories: LiveData<List<SportCategory>> = sportRepository.categories
    val currentUser: LiveData<User?> = userRepository.currentUser

    fun selectCategory(category: SportCategory) {
        _selectedCategory.value = category
        loadCategoryParticipants(category.id)
    }

    fun registerForCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                try {
                    // Register user for category
                    val userResult = userRepository.registerForCategory(userId, categoryId)
                    if (userResult.isSuccess) {
                        // Add participant to category
                        val categoryResult = sportRepository.addParticipantToCategory(categoryId, userId)
                        if (categoryResult.isSuccess) {
                            _registrationSuccess.value = true
                            // Refresh category data
                            _selectedCategory.value?.let { loadCategoryParticipants(it.id) }
                        } else {
                            _error.value = categoryResult.exceptionOrNull()?.message
                        }
                    } else {
                        _error.value = userResult.exceptionOrNull()?.message
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            } else {
                _error.value = "User not logged in"
                _isLoading.value = false
            }
        }
    }

    fun unregisterFromCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                try {
                    // Remove participant from category
                    val categoryResult = sportRepository.removeParticipantFromCategory(categoryId, userId)
                    if (categoryResult.isSuccess) {
                        // Update user's registered categories
                        val currentUser = userRepository.currentUser.value
                        if (currentUser != null) {
                            val updatedCategories = currentUser.registeredCategories.toMutableList()
                            updatedCategories.remove(categoryId)
                            val updatedUser = currentUser.copy(registeredCategories = updatedCategories)
                            userRepository.updateUser(updatedUser)
                        }
                        // Refresh category data
                        _selectedCategory.value?.let { loadCategoryParticipants(it.id) }
                    } else {
                        _error.value = categoryResult.exceptionOrNull()?.message
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                } finally {
                    _isLoading.value = false
                }
            } else {
                _error.value = "User not logged in"
                _isLoading.value = false
            }
        }
    }

    private fun loadCategoryParticipants(categoryId: String) {
        viewModelScope.launch {
            try {
                val participantIds = sportRepository.getParticipantsByCategory(categoryId)
                val allUsers = userRepository.users.value ?: emptyList()
                val participants = allUsers.filter { user -> participantIds.contains(user.id) }
                _categoryParticipants.value = participants
            } catch (e: Exception) {
                _error.value = e.message
            }
        }
    }

    fun isUserRegisteredForCategory(categoryId: String): Boolean {
        val currentUser = userRepository.currentUser.value
        return currentUser?.registeredCategories?.contains(categoryId) == true
    }

    fun canUserRegisterForCategory(category: SportCategory): Boolean {
        val currentParticipants = category.participants.size
        val maxParticipants = category.maxParticipants
        val isActive = category.isActive
        val registrationDeadline = category.registrationDeadline
        val currentTime = System.currentTimeMillis()
        
        return isActive && 
               currentParticipants < maxParticipants && 
               (registrationDeadline == 0L || currentTime < registrationDeadline) &&
               !isUserRegisteredForCategory(category.id)
    }

    fun clearError() {
        _error.value = null
    }

    fun clearRegistrationSuccess() {
        _registrationSuccess.value = false
    }
}