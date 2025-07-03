package com.example.sportsmanagement.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportsmanagement.data.model.Match
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

    private val _scoreUpdateSuccess = MutableLiveData<Boolean>()
    val scoreUpdateSuccess: LiveData<Boolean> = _scoreUpdateSuccess

    private val _filteredMatches = MutableLiveData<List<Match>>()
    val filteredMatches: LiveData<List<Match>> = _filteredMatches

    private var allMatchesList: List<Match> = emptyList()

    private val _selectedMatch = MutableLiveData<Match?>()
    val selectedMatch: LiveData<Match?> = _selectedMatch

    private val _userMatches = MutableLiveData<List<Match>>()
    val userMatches: LiveData<List<Match>> = _userMatches

    private val _categoryMatches = MutableLiveData<List<Match>>()
    val categoryMatches: LiveData<List<Match>> = _categoryMatches

    val allMatches: LiveData<List<Match>> = matchRepository.matches
    val liveMatches: LiveData<List<Match>> = matchRepository.liveMatches
    val currentUser: LiveData<User?> = userRepository.currentUser

    init {
        loadMatches()
    }

    fun loadMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val matches = matchRepository.getAllMatches()
                allMatchesList = matches
                _filteredMatches.value = matches
            } catch (e: Exception) {
                _error.value = "Failed to load matches: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun refreshMatches() {
        loadMatches()
    }

    fun filterMatches(status: Match.Status) {
        _filteredMatches.value = allMatchesList.filter { it.status == status }
    }

    fun showAllMatches() {
        _filteredMatches.value = allMatchesList
    }

    fun loadMatchDetails(matchId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val match = matchRepository.getMatchById(matchId)
                _selectedMatch.value = match
                // Start observing this specific match for real-time updates
                if (match != null) {
                    matchRepository.observeMatchById(match.id)
                }
            } catch (e: Exception) {
                _error.value = "Failed to load match details: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun startMatch(matchId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                matchRepository.updateMatchStatus(matchId, Match.Status.LIVE)
                _updateSuccess.value = true
                // Refresh the selected match
                loadMatchDetails(matchId)
            } catch (e: Exception) {
                _error.value = "Failed to start match: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun endMatch(matchId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val match = _selectedMatch.value
                if (match != null) {
                    // Determine winner based on score
                    val winnerId = when {
                        match.score.player1Score > match.score.player2Score -> match.player1Id
                        match.score.player2Score > match.score.player1Score -> match.player2Id
                        else -> null // Tie or no winner
                    }
                    
                    matchRepository.updateMatchStatus(matchId, Match.Status.COMPLETED)
                    if (winnerId != null) {
                        matchRepository.setMatchWinner(matchId, winnerId)
                    }
                    _updateSuccess.value = true
                    // Refresh the selected match
                    loadMatchDetails(matchId)
                } else {
                    _error.value = "Match not found"
                }
            } catch (e: Exception) {
                _error.value = "Failed to end match: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

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

    fun updateMatchScore(matchId: String, score: Match.Score) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                matchRepository.updateMatchScore(matchId, score)
                _scoreUpdateSuccess.value = true
                // Refresh the selected match to get updated data
                loadMatchDetails(matchId)
            } catch (e: Exception) {
                _error.value = "Failed to update score: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateMatchStatus(matchId: String, status: Match.Status) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                matchRepository.updateMatchStatus(matchId, status)
                _updateSuccess.value = true
                // Refresh the selected match to get updated data
                loadMatchDetails(matchId)
            } catch (e: Exception) {
                _error.value = "Failed to update status: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setMatchWinner(matchId: String, winnerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                matchRepository.setMatchWinner(matchId, winnerId)
                _updateSuccess.value = true
                // Refresh the selected match to get updated data
                loadMatchDetails(matchId)
            } catch (e: Exception) {
                _error.value = "Failed to set winner: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createMatch(match: Match) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                matchRepository.createMatch(match)
                _updateSuccess.value = true
                // Refresh matches list
                loadMatches()
            } catch (e: Exception) {
                _error.value = "Failed to create match: ${e.message}"
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

    fun clearScoreUpdateSuccess() {
        _scoreUpdateSuccess.value = false
    }
}