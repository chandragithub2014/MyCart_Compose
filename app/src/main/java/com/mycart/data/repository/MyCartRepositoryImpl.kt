package com.mycart.data.repository

import com.mycart.data.database.MyCartDAO
import com.mycart.data.mock.MockAPI
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
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

}