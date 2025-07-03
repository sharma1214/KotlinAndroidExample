package com.example.sportsmanagement.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sportsmanagement.data.model.SportCategory
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class SportRepository {
    private val database = FirebaseDatabase.getInstance()
    private val categoriesRef = database.getReference("sport_categories")

    private val _categories = MutableLiveData<List<SportCategory>>()
    val categories: LiveData<List<SportCategory>> = _categories

    private val _selectedCategory = MutableLiveData<SportCategory?>()
    val selectedCategory: LiveData<SportCategory?> = _selectedCategory

    init {
        observeCategories()
        initializeDefaultCategories()
    }

    private fun initializeDefaultCategories() {
        val defaultCategories = listOf(
            SportCategory(
                id = "chess",
                name = "Chess",
                description = "Strategic board game for two players",
                maxParticipants = 32,
                isActive = true
            ),
            SportCategory(
                id = "carom",
                name = "Carom",
                description = "Indoor cue sport played on a pocketless table",
                maxParticipants = 16,
                isActive = true
            ),
            SportCategory(
                id = "table_tennis",
                name = "Table Tennis",
                description = "Fast-paced racket sport",
                maxParticipants = 32,
                isActive = true
            ),
            SportCategory(
                id = "badminton",
                name = "Badminton",
                description = "Racquet sport played with shuttlecock",
                maxParticipants = 32,
                isActive = true
            ),
            SportCategory(
                id = "cricket",
                name = "Cricket",
                description = "Team sport played with bat and ball",
                maxParticipants = 22,
                isActive = true
            ),
            SportCategory(
                id = "billiards",
                name = "Billiards",
                description = "Cue sport played on a table with pockets",
                maxParticipants = 16,
                isActive = true
            )
        )

        // Check if categories exist, if not initialize them
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    defaultCategories.forEach { category ->
                        categoriesRef.child(category.id).setValue(category)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    suspend fun createCategory(category: SportCategory): Result<SportCategory> {
        return try {
            val categoryId = if (category.id.isEmpty()) {
                categoriesRef.push().key ?: return Result.failure(Exception("Failed to generate ID"))
            } else {
                category.id
            }
            val categoryWithId = category.copy(id = categoryId)
            categoriesRef.child(categoryId).setValue(categoryWithId).await()
            Result.success(categoryWithId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCategory(category: SportCategory): Result<SportCategory> {
        return try {
            categoriesRef.child(category.id).setValue(category).await()
            Result.success(category)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCategoryById(categoryId: String): SportCategory? {
        return try {
            val snapshot = categoriesRef.child(categoryId).get().await()
            val category = snapshot.getValue(SportCategory::class.java)
            _selectedCategory.value = category
            category
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addParticipantToCategory(categoryId: String, participantId: String): Result<Boolean> {
        return try {
            val snapshot = categoriesRef.child(categoryId).get().await()
            val category = snapshot.getValue(SportCategory::class.java)
            if (category != null) {
                val participants = category.participants.toMutableList()
                if (!participants.contains(participantId) && participants.size < category.maxParticipants) {
                    participants.add(participantId)
                    val updatedCategory = category.copy(participants = participants)
                    categoriesRef.child(categoryId).setValue(updatedCategory).await()
                    Result.success(true)
                } else {
                    Result.failure(Exception("Cannot add participant"))
                }
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun removeParticipantFromCategory(categoryId: String, participantId: String): Result<Boolean> {
        return try {
            val snapshot = categoriesRef.child(categoryId).get().await()
            val category = snapshot.getValue(SportCategory::class.java)
            if (category != null) {
                val participants = category.participants.toMutableList()
                participants.remove(participantId)
                val updatedCategory = category.copy(participants = participants)
                categoriesRef.child(categoryId).setValue(updatedCategory).await()
                Result.success(true)
            } else {
                Result.failure(Exception("Category not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getParticipantsByCategory(categoryId: String): List<String> {
        return try {
            val snapshot = categoriesRef.child(categoryId).get().await()
            val category = snapshot.getValue(SportCategory::class.java)
            category?.participants ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun observeCategories() {
        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categoriesList = mutableListOf<SportCategory>()
                for (categorySnapshot in snapshot.children) {
                    categorySnapshot.getValue(SportCategory::class.java)?.let { category ->
                        categoriesList.add(category)
                    }
                }
                _categories.value = categoriesList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    suspend fun deleteCategory(categoryId: String): Result<Boolean> {
        return try {
            categoriesRef.child(categoryId).removeValue().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}