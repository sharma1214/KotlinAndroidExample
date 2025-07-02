package com.example.sportsmanagement.data.model

data class Match(
    val id: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val player1Id: String = "",
    val player1Name: String = "",
    val player2Id: String = "",
    val player2Name: String = "",
    val scheduledTime: Long = 0,
    val status: MatchStatus = MatchStatus.SCHEDULED,
    val score: MatchScore = MatchScore(),
    val winnerId: String = "",
    val winnerName: String = "",
    val round: String = "",
    val venue: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class MatchScore(
    val player1Score: Int = 0,
    val player2Score: Int = 0,
    val sets: List<SetScore> = emptyList(),
    val currentSet: Int = 1
)

data class SetScore(
    val setNumber: Int = 1,
    val player1Score: Int = 0,
    val player2Score: Int = 0
)

enum class MatchStatus {
    SCHEDULED,
    LIVE,
    COMPLETED,
    CANCELLED,
    POSTPONED
}