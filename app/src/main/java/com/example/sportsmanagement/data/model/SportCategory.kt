package com.example.sportsmanagement.data.model

data class SportCategory(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val maxParticipants: Int = 0,
    val registrationDeadline: Long = 0,
    val isActive: Boolean = true,
    val participants: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val CHESS = "chess"
        const val CAROM = "carom"
        const val TABLE_TENNIS = "table_tennis"
        const val BADMINTON = "badminton"
        const val CRICKET = "cricket"
        const val BILLIARDS = "billiards"
    }
}