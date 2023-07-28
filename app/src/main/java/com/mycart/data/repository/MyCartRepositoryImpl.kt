package com.mycart.data.repository

import com.mycart.data.mock.MockAPI
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.repository.MyCartRepository

class MyCartRepositoryImpl(private val mockAPI: MockAPI) : MyCartRepository {
    override suspend fun fetchCategoryDetails(): List<Category>  =  mockAPI.getCategoryDetails()
    override suspend fun fetchSeasonalCategoryDetails(): List<Category>  = mockAPI.getSeasonalCategoryDetails()
    override suspend fun fetchDeals(): List<Deal>  = mockAPI.getDeals()

}