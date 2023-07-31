package com.mycart.domain.repository

import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.model.User
import kotlinx.coroutines.flow.Flow

interface MyCartRepository {
    suspend fun fetchCategoryDetails(): List<Category>
    suspend fun fetchSeasonalCategoryDetails():List<Category>
    suspend fun fetchDeals():List<Deal>
    suspend fun getAllUsers(): Flow<List<User>>
    suspend fun insert(user: User):Long
    suspend fun isUserAvailable(email:String):Boolean
    suspend fun isValidLoggedInUser(email: String,password:String):User?
    suspend fun fetchUserInfoByEmail(email: String):User?
}