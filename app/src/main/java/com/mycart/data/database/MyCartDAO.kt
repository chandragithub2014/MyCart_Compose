package com.mycart.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mycart.domain.model.Category
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface MyCartDAO {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE userEmail = :email")
    suspend fun isUserAvailable(email: String): User?

    @Query("SELECT * FROM users WHERE userEmail = :email AND userPassword = :password")
    suspend fun getLoggedInUserInfo(email: String, password: String): User?

    @Query("SELECT * FROM users WHERE userEmail = :email")
    suspend fun getLoggedInUserInfoByEmail(email: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Query("SELECT * from category where categoryName=:categoryName AND storeName = :storeName")
    suspend fun isCategoryAvailable(categoryName: String,storeName:String): Category?

    @Query("SELECT * from category where storeLoc = :storeLocation AND storeName = :storeName AND userEmail = :email")
    suspend fun fetchCategories(
        storeLocation: String,
        storeName: String,
        email: String
    ): List<Category>

    @Query("SELECT * from category where storeLoc = :storeLocation AND storeName = :storeName AND userEmail = :email AND isDeal = true")
    suspend fun fetchDeals(storeLocation: String, storeName: String, email: String): List<Category>

    @Query("SELECT * from category where storeLoc = :storeLocation AND storeName = :storeName AND userEmail = :email AND isSeasonal = true")
    suspend fun fetchSeasonalDeals(
        storeLocation: String,
        storeName: String,
        email: String
    ): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: Store): Long

    @Query("SELECT * from store")
    suspend fun fetchStores(): List<Store>

    @Query("SELECT * from store where ownerEmail = :email")
    suspend fun fetchStoreByEmail(email: String): Store?

    @Query("SELECT * from store where ownerEmail = :email AND storeName = :store")
    suspend fun isStoreAvailable(email: String, store: String): Store?

    @Query("SELECT * from category where  storeName = :storeName")
    suspend fun fetchCategoriesByStore(storeName: String): List<Category>

    @Query("SELECT * from category where storeName = :storeName AND isDeal = true")
    suspend fun fetchDealsByStore(storeName: String): List<Category>

    @Query("SELECT * from category where  storeName = :storeName  AND isSeasonal = true")
    suspend fun fetchSeasonalDealsByStore(storeName: String): List<Category>
}