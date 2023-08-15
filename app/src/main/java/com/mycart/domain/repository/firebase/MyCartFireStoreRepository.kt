package com.mycart.domain.repository.firebase

import com.mycart.domain.model.Store
import com.mycart.domain.model.User
import com.mycart.ui.common.Response

typealias AddUserResponse = Response<Boolean>
typealias AddStoreResponse = Response<Boolean>

interface MyCartFireStoreRepository {

    suspend fun addUserToFireStore(user: User): AddUserResponse
    suspend fun createStore(store:Store): AddStoreResponse
    suspend fun fetchAllStores():List<Store>
    suspend fun checkForAdmin(email:String):User?
    suspend fun fetchStoreByEmail(email:String):Store?
}

