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

class ProfileViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val sportRepository = SportRepository()
    private val matchRepository = MatchRepository()

    // LiveData for user profile
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _registeredCategories = MutableLiveData<List<SportCategory>>()
    val registeredCategories: LiveData<List<SportCategory>> = _registeredCategories

    private val _matchHistory = MutableLiveData<List<Match>>()
    val matchHistory: LiveData<List<Match>> = _matchHistory

    private val _userStats = MutableLiveData<UserStats>()
    val userStats: LiveData<UserStats> = _userStats

    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    data class UserStats(
        val totalMatches: Int,
        val wonMatches: Int,
        val lostMatches: Int,
        val winPercentage: Int
    )

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load current user
                val user = userRepository.getCurrentUser()
                _currentUser.value = user
                
                if (user != null) {
                    // Load registered categories
                    loadRegisteredCategories(user.registeredCategories)
                    
                    // Load match history
                    loadMatchHistory(user.id)
                    
                    // Calculate user stats
                    calculateUserStats(user.id)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadRegisteredCategories(categoryIds: List<String>) {
        try {
            val categories = sportRepository.getCategoriesByIds(categoryIds)
            _registeredCategories.value = categories
        } catch (e: Exception) {
            _error.value = "Failed to load registered categories: ${e.message}"
        }
    }

    private suspend fun loadMatchHistory(userId: String) {
        try {
            val matches = matchRepository.getMatchesByUserId(userId)
            _matchHistory.value = matches.sortedByDescending { it.scheduledTime }
        } catch (e: Exception) {
            _error.value = "Failed to load match history: ${e.message}"
        }
    }

    private suspend fun calculateUserStats(userId: String) {
        try {
            val matches = matchRepository.getMatchesByUserId(userId)
            val completedMatches = matches.filter { it.status == Match.Status.COMPLETED }
            
            val totalMatches = completedMatches.size
            val wonMatches = completedMatches.count { it.winnerId == userId }
            val lostMatches = totalMatches - wonMatches
            val winPercentage = if (totalMatches > 0) {
                (wonMatches * 100) / totalMatches
            } else {
                0
            }
            
            _userStats.value = UserStats(
                totalMatches = totalMatches,
                wonMatches = wonMatches,
                lostMatches = lostMatches,
                winPercentage = winPercentage
            )
        } catch (e: Exception) {
            _error.value = "Failed to calculate user stats: ${e.message}"
        }
    }

    fun toggleRegistration(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = _currentUser.value
                if (user != null) {
                    val isCurrentlyRegistered = user.registeredCategories.contains(categoryId)
                    
                    if (isCurrentlyRegistered) {
                        // Unregister from category
                        userRepository.unregisterFromCategory(user.id, categoryId)
                        sportRepository.removeParticipantFromCategory(categoryId, user.id)
                    } else {
                        // Register for category
                        userRepository.registerForCategory(user.id, categoryId)
                        sportRepository.addParticipantToCategory(categoryId, user.id)
                    }
                    
                    // Reload user data
                    loadUserProfile()
                }
            } catch (e: Exception) {
                _error.value = "Failed to update registration: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, email: String, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = _currentUser.value
                if (user != null) {
                    val updatedUser = user.copy(
                        name = name,
                        email = email,
                        phone = phone
                    )
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                }
            } catch (e: Exception) {
                _error.value = "Failed to update profile: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateProfileImage(imageUrl: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = _currentUser.value
                if (user != null) {
                    val updatedUser = user.copy(profileImageUrl = imageUrl)
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                }
            } catch (e: Exception) {
                _error.value = "Failed to update profile image: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshProfile() {
        loadUserProfile()
    }

    fun clearError() {
        _error.value = null
    }
}