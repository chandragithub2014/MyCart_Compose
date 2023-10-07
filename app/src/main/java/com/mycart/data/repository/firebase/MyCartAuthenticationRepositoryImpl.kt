package com.mycart.data.repository.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mycart.domain.model.User
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class MyCartAuthenticationRepositoryImpl() : MyCartAuthenticationRepository{

    private val auth = FirebaseAuth.getInstance()

    override  fun getCurrentUser(): Flow<User?> = flow {
        val currentUser = auth.currentUser
        emit(currentUser?.toUser())
    }

    override suspend fun signIn(email: String, password: String): FirebaseUser? =  try {
        val signInResult  =  auth.signInWithEmailAndPassword(email, password).await()
        signInResult.user
    } catch (e: Exception) {
        println("${e.printStackTrace()}")
        null
    }

    override suspend fun signUp(email: String, password: String): FirebaseUser? = try {
        val signUpResult = auth.createUserWithEmailAndPassword(email, password).await()
          signUpResult.user
    }catch (e: Exception) {
           null
    }

    override suspend fun isUserLoggedIn(): FirebaseUser?  = auth.currentUser


    /* suspend fun override fun isUserLoggedIn(): FirebaseUser?  = try{
          auth.currentUser
      }catch (e: Exception) {
          null
      }
  */

    override fun signOut() : Boolean {
        auth.signOut()
        auth.currentUser?.let {
            return false
        }?:run{
            return true
        }
    }

    override suspend fun resetPassword(email: String): Boolean {
        return try {
            auth.sendPasswordResetEmail(email).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            User(userEmail = it.email ?: "")
        }
    }
}