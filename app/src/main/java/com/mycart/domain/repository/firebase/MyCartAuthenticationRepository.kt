package com.mycart.domain.repository.firebase

import com.google.firebase.auth.FirebaseUser
import com.mycart.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MyCartAuthenticationRepository {
    fun getCurrentUser(): Flow<User?>
    suspend fun signIn(email: String, password: String): FirebaseUser?
    suspend fun signUp(email: String, password: String): FirebaseUser?
    fun signOut()
}