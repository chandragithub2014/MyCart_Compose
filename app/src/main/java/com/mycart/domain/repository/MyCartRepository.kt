package com.mycart.domain.repository

import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal

interface MyCartRepository {
    suspend fun fetchCategoryDetails(): List<Category>
    suspend fun fetchSeasonalCategoryDetails():List<Category>
    suspend fun fetchDeals():List<Deal>
}