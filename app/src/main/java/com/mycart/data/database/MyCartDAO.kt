package com.mycart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycart.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MyCartDAO {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE userEmail = :email")
    suspend fun isUserAvailable(email:String):User?

    @Query("SELECT * FROM users WHERE userEmail = :email AND userPassword = :password")
    suspend fun getLoggedInUserInfo(email: String, password: String): User?

}