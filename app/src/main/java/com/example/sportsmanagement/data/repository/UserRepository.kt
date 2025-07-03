package com.example.sportsmanagement.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sportsmanagement.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    init {
        auth.currentUser?.let { firebaseUser ->
            getCurrentUser(firebaseUser.uid)
        }
    }

    suspend fun registerUser(email: String, password: String, user: User): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val userWithId = user.copy(id = firebaseUser.uid)
                usersRef.child(firebaseUser.uid).setValue(userWithId).await()
                _currentUser.value = userWithId
                Result.success(userWithId)
            } else {
                Result.failure(Exception("Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user
            if (firebaseUser != null) {
                val user = getCurrentUser(firebaseUser.uid)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getCurrentUser(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            val user = snapshot.getValue(User::class.java)
            _currentUser.value = user
            user
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User): Result<User> {
        return try {
            usersRef.child(user.id).setValue(user).await()
            _currentUser.value = user
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerForCategory(userId: String, categoryId: String): Result<Boolean> {
        return try {
            val userSnapshot = usersRef.child(userId).get().await()
            val user = userSnapshot.getValue(User::class.java)
            if (user != null) {
                val updatedCategories = user.registeredCategories.toMutableList()
                if (!updatedCategories.contains(categoryId)) {
                    updatedCategories.add(categoryId)
                    val updatedUser = user.copy(registeredCategories = updatedCategories)
                    usersRef.child(userId).setValue(updatedUser).await()
                    _currentUser.value = updatedUser
                }
                Result.success(true)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeUsers() {
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usersList = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    userSnapshot.getValue(User::class.java)?.let { user ->
                        usersList.add(user)
                    }
                }
                _users.value = usersList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    fun signOut() {
        auth.signOut()
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}