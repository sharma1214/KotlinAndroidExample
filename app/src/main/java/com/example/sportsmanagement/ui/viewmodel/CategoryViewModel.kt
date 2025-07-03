package com.example.sportsmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.SportCategory
import com.example.sportsmanagement.data.model.User
import com.example.sportsmanagement.data.repository.MatchRepository
import com.example.sportsmanagement.data.repository.SportRepository
import com.example.sportsmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val sportRepository = SportRepository()
    private val userRepository = UserRepository()
    private val matchRepository = MatchRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _registrationSuccess = MutableLiveData<String?>()
    val registrationSuccess: LiveData<String?> = _registrationSuccess

    private val _categories = MutableLiveData<List<SportCategory>>()
    val categories: LiveData<List<SportCategory>> = _categories

    private val _selectedCategory = MutableLiveData<SportCategory?>()
    val selectedCategory: LiveData<SportCategory?> = _selectedCategory

    private val _categoryParticipants = MutableLiveData<List<User>>()
    val categoryParticipants: LiveData<List<User>> = _categoryParticipants

    private val _categoryMatches = MutableLiveData<List<Match>>()
    val categoryMatches: LiveData<List<Match>> = _categoryMatches

    val currentUser: LiveData<User?> = userRepository.currentUser

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val categories = sportRepository.getAllCategories()
                _categories.value = categories
            } catch (e: Exception) {
                _error.value = "Failed to load categories: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshCategories() {
        loadCategories()
    }

    fun loadCategoryDetails(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val category = sportRepository.getCategoryById(categoryId)
                _selectedCategory.value = category
                if (category != null) {
                    loadCategoryParticipants(category.id)
                    loadCategoryMatches(category.id)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load category details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCategoryMatches(categoryId: String) {
        viewModelScope.launch {
            try {
                val matches = matchRepository.getMatchesByCategory(categoryId)
                _categoryMatches.value = matches
            } catch (e: Exception) {
                _error.value = "Failed to load category matches: ${e.message}"
            }
        }
    }

    fun toggleRegistration(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = currentUser.value
                if (user != null) {
                    val isRegistered = user.registeredCategories.contains(categoryId)
                    if (isRegistered) {
                        unregisterFromCategory(categoryId)
                    } else {
                        registerForCategory(categoryId)
                    }
                } else {
                    _error.value = "User not logged in"
                }
            } catch (e: Exception) {
                _error.value = "Failed to update registration: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(category: SportCategory) {
        _selectedCategory.value = category
        loadCategoryParticipants(category.id)
        loadCategoryMatches(category.id)
    }

    fun registerForCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = userRepository.getCurrentUserId()
            if (userId != null) {
                try {
                    // Register user for category
                    val userResult = userRepository.registerForCategory(userId, categoryId)
                    userRepository.registerForCategory(userId, categoryId)
                    sportRepository.addParticipantToCategory(categoryId, userId)
                    _registrationSuccess.value = "Successfully registered for category"
                    // Refresh data
                    loadCategories()
                    _selectedCategory.value?.let { loadCategoryDetails(it.id) }
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
                    sportRepository.removeParticipantFromCategory(categoryId, userId)
                    userRepository.unregisterFromCategory(userId, categoryId)
                    _registrationSuccess.value = "Successfully unregistered from category"
                    // Refresh data
                    loadCategories()
                    _selectedCategory.value?.let { loadCategoryDetails(it.id) }
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
        _registrationSuccess.value = null
    }
}