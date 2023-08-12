package com.mycart.domain.repository

import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.model.Store
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
    suspend fun createCategory(category: Category):Long
    suspend fun isCategoryAvailable(categoryName:String,storeName:String):Boolean
    suspend fun fetchAllCategories(storeLoc:String,storeName:String,email:String):List<Category>
    suspend fun fetchAllDeals(storeLoc:String,storeName:String,email:String):List<Category>
    suspend fun fetchSeasonalDeals(storeLoc:String,storeName:String,email:String):List<Category>
    suspend fun fetchStores():List<Store>
    suspend fun createStore(store:Store) : Long
    suspend fun fetchStoreByEmail(email:String):Store?
    suspend fun isStoreAvailable(email:String,store:String):Boolean
    suspend fun fetchCategoriesByStore(storeName:String):List<Category>
    suspend fun fetchDealsByStore(storeName:String):List<Category>
    suspend fun fetchSeasonalDetalsByStore(storeName:String):List<Category>
    suspend fun deleteCategoryByStore(categoryName: String,store: String):Int
    suspend fun fetchCategoryInfo(categoryName: String,store: String): Category?
    suspend fun editCategoryInfo(categoryName: String,store: String,isDeal:Boolean,isSeasonal:Boolean,dealInfo:String):Int
}