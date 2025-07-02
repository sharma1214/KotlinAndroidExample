package com.example.sportsmanagement.data.model

data class Tournament(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val sportCategoryId: String = "",
    val startDate: Long = 0,
    val endDate: Long = 0,
    val registrationDeadline: Long = 0,
    val maxParticipants: Int = 0,
    val entryFee: Double = 0.0,
    val prizePool: String = "",
    val rules: String = "",
    val venue: String = "",
    val status: TournamentStatus = TournamentStatus.UPCOMING,
    val participants: List<String> = emptyList(),
    val matches: List<String> = emptyList(),
    val winnerId: String = "",
    val runnerUpId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class TournamentStatus {
    UPCOMING,
    REGISTRATION_OPEN,
    REGISTRATION_CLOSED,
    ONGOING,
    COMPLETED,
    CANCELLED
}