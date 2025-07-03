package com.example.sportsmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.MatchScore
import com.example.sportsmanagement.data.model.MatchStatus
import com.example.sportsmanagement.data.model.User
import com.example.sportsmanagement.data.repository.MatchRepository
import com.example.sportsmanagement.data.repository.UserRepository
import kotlinx.coroutines.launch

class MatchViewModel : ViewModel() {
    private val matchRepository = MatchRepository()
    private val userRepository = UserRepository()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    private val _selectedMatch = MutableLiveData<Match?>()
    val selectedMatch: LiveData<Match?> = _selectedMatch

    private val _userMatches = MutableLiveData<List<Match>>()
    val userMatches: LiveData<List<Match>> = _userMatches

    private val _categoryMatches = MutableLiveData<List<Match>>()
    val categoryMatches: LiveData<List<Match>> = _categoryMatches

    val allMatches: LiveData<List<Match>> = matchRepository.matches
    val liveMatches: LiveData<List<Match>> = matchRepository.liveMatches
    val currentUser: LiveData<User?> = userRepository.currentUser

    fun selectMatch(match: Match) {
        _selectedMatch.value = match
        // Start observing this specific match for real-time updates
        matchRepository.observeMatchById(match.id)
    }

    fun loadUserMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val userId = userRepository.getCurrentUserId()
                if (userId != null) {
                    val matches = matchRepository.getMatchesByPlayer(userId)
                    _userMatches.value = matches
                } else {
                    _error.value = "User not logged in"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMatchesByCategory(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val matches = matchRepository.getMatchesByCategory(categoryId)
                _categoryMatches.value = matches
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMatchScore(matchId: String, score: MatchScore) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = matchRepository.updateMatchScore(matchId, score)
                if (result.isSuccess) {
                    _updateSuccess.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMatchStatus(matchId: String, status: MatchStatus) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = matchRepository.updateMatchStatus(matchId, status)
                if (result.isSuccess) {
                    _updateSuccess.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setMatchWinner(matchId: String, winnerId: String, winnerName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = matchRepository.setMatchWinner(matchId, winnerId, winnerName)
                if (result.isSuccess) {
                    _updateSuccess.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMatch(match: Match) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = matchRepository.createMatch(match)
                if (result.isSuccess) {
                    _updateSuccess.value = true
                } else {
                    _error.value = result.exceptionOrNull()?.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun isUserAdmin(): Boolean {
        return currentUser.value?.isAdmin == true
    }

    fun canUserUpdateMatch(match: Match): Boolean {
        val currentUser = this.currentUser.value
        return currentUser?.isAdmin == true || 
               match.player1Id == currentUser?.id || 
               match.player2Id == currentUser?.id
    }

    fun getTodaysMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val matches = matchRepository.getTodaysMatches()
                _categoryMatches.value = matches
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getUpcomingMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val matches = matchRepository.getUpcomingMatches()
                _categoryMatches.value = matches
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getCompletedMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val matches = matchRepository.getCompletedMatches()
                _categoryMatches.value = matches
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearUpdateSuccess() {
        _updateSuccess.value = false
    }
}