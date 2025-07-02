package com.example.sportsmanagement.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sportsmanagement.data.model.Match
import com.example.sportsmanagement.data.model.MatchStatus
import com.example.sportsmanagement.data.model.MatchScore
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class MatchRepository {
    private val database = FirebaseDatabase.getInstance()
    private val matchesRef = database.getReference("matches")

    private val _matches = MutableLiveData<List<Match>>()
    val matches: LiveData<List<Match>> = _matches

    private val _liveMatches = MutableLiveData<List<Match>>()
    val liveMatches: LiveData<List<Match>> = _liveMatches

    private val _selectedMatch = MutableLiveData<Match?>()
    val selectedMatch: LiveData<Match?> = _selectedMatch

    init {
        observeMatches()
        observeLiveMatches()
    }

    suspend fun createMatch(match: Match): Result<Match> {
        return try {
            val matchId = matchesRef.push().key ?: return Result.failure(Exception("Failed to generate ID"))
            val matchWithId = match.copy(id = matchId)
            matchesRef.child(matchId).setValue(matchWithId).await()
            Result.success(matchWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatch(match: Match): Result<Match> {
        return try {
            val updatedMatch = match.copy(updatedAt = System.currentTimeMillis())
            matchesRef.child(match.id).setValue(updatedMatch).await()
            Result.success(updatedMatch)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchScore(matchId: String, score: MatchScore): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "score" to score,
                "updatedAt" to System.currentTimeMillis()
            )
            matchesRef.child(matchId).updateChildren(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateMatchStatus(matchId: String, status: MatchStatus): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "status" to status.name,
                "updatedAt" to System.currentTimeMillis()
            )
            matchesRef.child(matchId).updateChildren(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setMatchWinner(matchId: String, winnerId: String, winnerName: String): Result<Boolean> {
        return try {
            val updates = hashMapOf<String, Any>(
                "winnerId" to winnerId,
                "winnerName" to winnerName,
                "status" to MatchStatus.COMPLETED.name,
                "updatedAt" to System.currentTimeMillis()
            )
            matchesRef.child(matchId).updateChildren(updates).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMatchById(matchId: String): Match? {
        return try {
            val snapshot = matchesRef.child(matchId).get().await()
            val match = snapshot.getValue(Match::class.java)
            _selectedMatch.value = match
            match
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getMatchesByCategory(categoryId: String): List<Match> {
        return try {
            val snapshot = matchesRef.orderByChild("categoryId").equalTo(categoryId).get().await()
            val matchesList = mutableListOf<Match>()
            for (matchSnapshot in snapshot.children) {
                matchSnapshot.getValue(Match::class.java)?.let { match ->
                    matchesList.add(match)
                }
            }
            matchesList.sortedBy { it.scheduledTime }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMatchesByPlayer(playerId: String): List<Match> {
        return try {
            val snapshot = matchesRef.get().await()
            val matchesList = mutableListOf<Match>()
            for (matchSnapshot in snapshot.children) {
                matchSnapshot.getValue(Match::class.java)?.let { match ->
                    if (match.player1Id == playerId || match.player2Id == playerId) {
                        matchesList.add(match)
                    }
                }
            }
            matchesList.sortedBy { it.scheduledTime }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTodaysMatches(): List<Match> {
        return try {
            val todayStart = System.currentTimeMillis() - (System.currentTimeMillis() % (24 * 60 * 60 * 1000))
            val todayEnd = todayStart + (24 * 60 * 60 * 1000)
            
            val snapshot = matchesRef.orderByChild("scheduledTime")
                .startAt(todayStart.toDouble())
                .endAt(todayEnd.toDouble())
                .get().await()
            
            val matchesList = mutableListOf<Match>()
            for (matchSnapshot in snapshot.children) {
                matchSnapshot.getValue(Match::class.java)?.let { match ->
                    matchesList.add(match)
                }
            }
            matchesList.sortedBy { it.scheduledTime }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getUpcomingMatches(): List<Match> {
        return try {
            val currentTime = System.currentTimeMillis()
            val snapshot = matchesRef.orderByChild("scheduledTime")
                .startAt(currentTime.toDouble())
                .get().await()
            
            val matchesList = mutableListOf<Match>()
            for (matchSnapshot in snapshot.children) {
                matchSnapshot.getValue(Match::class.java)?.let { match ->
                    if (match.status == MatchStatus.SCHEDULED) {
                        matchesList.add(match)
                    }
                }
            }
            matchesList.sortedBy { it.scheduledTime }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getCompletedMatches(): List<Match> {
        return try {
            val snapshot = matchesRef.orderByChild("status").equalTo(MatchStatus.COMPLETED.name).get().await()
            val matchesList = mutableListOf<Match>()
            for (matchSnapshot in snapshot.children) {
                matchSnapshot.getValue(Match::class.java)?.let { match ->
                    matchesList.add(match)
                }
            }
            matchesList.sortedByDescending { it.updatedAt }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun observeMatches() {
        matchesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val matchesList = mutableListOf<Match>()
                for (matchSnapshot in snapshot.children) {
                    matchSnapshot.getValue(Match::class.java)?.let { match ->
                        matchesList.add(match)
                    }
                }
                _matches.value = matchesList.sortedBy { it.scheduledTime }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun observeLiveMatches() {
        matchesRef.orderByChild("status").equalTo(MatchStatus.LIVE.name)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val liveMatchesList = mutableListOf<Match>()
                    for (matchSnapshot in snapshot.children) {
                        matchSnapshot.getValue(Match::class.java)?.let { match ->
                            liveMatchesList.add(match)
                        }
                    }
                    _liveMatches.value = liveMatchesList
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
    }

    fun observeMatchById(matchId: String) {
        matchesRef.child(matchId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val match = snapshot.getValue(Match::class.java)
                _selectedMatch.value = match
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    suspend fun deleteMatch(matchId: String): Result<Boolean> {
        return try {
            matchesRef.child(matchId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}