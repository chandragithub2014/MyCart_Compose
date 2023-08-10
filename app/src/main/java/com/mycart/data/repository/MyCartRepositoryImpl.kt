package com.mycart.data.repository

import com.mycart.data.database.MyCartDAO
import com.mycart.data.mock.MockAPI
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.domain.repository.MyCartRepository

class MyCartRepositoryImpl(private val mockAPI: MockAPI, private val myCartDAO: MyCartDAO) :
    MyCartRepository {
    override suspend fun fetchCategoryDetails(): List<Category> = mockAPI.getCategoryDetails()
    override suspend fun fetchSeasonalCategoryDetails(): List<Category> =
        mockAPI.getSeasonalCategoryDetails()

    override suspend fun fetchDeals(): List<Deal> = mockAPI.getDeals()
    override suspend fun getAllUsers() = myCartDAO.getAllUsers()
    override suspend fun insert(user: User) = myCartDAO.insert(user)
    override suspend fun isUserAvailable(email: String): Boolean {
        val user = myCartDAO.isUserAvailable(email)
        return user != null
    }

    override suspend fun isValidLoggedInUser(email: String, password: String): User? {
        return myCartDAO.getLoggedInUserInfo(email, password)
    }

    override suspend fun fetchUserInfoByEmail(email: String): User? {
        return myCartDAO.getLoggedInUserInfoByEmail(email)
    }

    override suspend fun createCategory(category: Category) = myCartDAO.insertCategory(category)
    override suspend fun isCategoryAvailable(name: String): Boolean {
        val category = myCartDAO.isCategoryAvailable(name)
        return category != null
    }

    override suspend fun fetchAllCategories(storeLoc: String, storeName: String, email: String) =
        myCartDAO.fetchCategories(storeLoc, storeName, email)

    override suspend fun fetchAllDeals(
        storeLoc: String,
        storeName: String,
        email: String
    ) = myCartDAO.fetchDeals(storeLoc,storeName,email)

    override suspend fun fetchSeasonalDeals(
        storeLoc: String,
        storeName: String,
        email: String
    ) = myCartDAO.fetchSeasonalDeals(storeLoc,storeName,email)

    override suspend fun fetchStores()  = myCartDAO.fetchStores()

    override suspend fun createStore(store: Store)   = myCartDAO.insertStore(store)

    override suspend fun fetchStoreByEmail(email: String) = myCartDAO.fetchStoreByEmail(email)

    override suspend fun isStoreAvailable(email: String, store: String): Boolean {
        val storeInfo = myCartDAO.isStoreAvailable(email,store)
        return storeInfo != null
    }
}