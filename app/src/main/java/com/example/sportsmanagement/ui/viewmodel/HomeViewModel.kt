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

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val sportRepository = SportRepository()
    private val matchRepository = MatchRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _todaysMatches = MutableLiveData<List<Match>>()
    val todaysMatches: LiveData<List<Match>> = _todaysMatches

    private val _liveMatches = MutableLiveData<List<Match>>()
    val liveMatches: LiveData<List<Match>> = _liveMatches

    private val _upcomingMatches = MutableLiveData<List<Match>>()
    val upcomingMatches: LiveData<List<Match>> = _upcomingMatches

    private val _recentResults = MutableLiveData<List<Match>>()
    val recentResults: LiveData<List<Match>> = _recentResults

    private val _featuredCategories = MutableLiveData<List<SportCategory>>()
    val featuredCategories: LiveData<List<SportCategory>> = _featuredCategories

    val currentUser: LiveData<User?> = userRepository.currentUser
    val categories: LiveData<List<SportCategory>> = sportRepository.categories

    init {
        loadDashboardData()
        observeLiveMatches()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Load today's matches
                val todaysMatches = matchRepository.getTodaysMatches()
                _todaysMatches.value = todaysMatches

                // Load upcoming matches (next 7 days)
                val upcomingMatches = matchRepository.getUpcomingMatches().take(10)
                _upcomingMatches.value = upcomingMatches

                // Load recent results
                val recentResults = matchRepository.getCompletedMatches().take(5)
                _recentResults.value = recentResults

                // Load featured categories (top 4)
                val allCategories = sportRepository.categories.value ?: emptyList()
                _featuredCategories.value = allCategories.take(4)

            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun observeLiveMatches() {
        matchRepository.liveMatches.observeForever { matches ->
            _liveMatches.value = matches
        }
    }

    fun refreshData() {
        loadDashboardData()
    }

    fun clearError() {
        _error.value = null
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up observers if needed
    }
}