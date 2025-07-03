package com.example.sportsmanagement.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImageUrl: String = "",
    val isAdmin: Boolean = false,
    val registeredCategories: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)