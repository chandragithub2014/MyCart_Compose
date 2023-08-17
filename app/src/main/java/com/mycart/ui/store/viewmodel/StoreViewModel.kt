package com.mycart.ui.store.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Store
import com.mycart.domain.repository.MyCartRepository
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class StoreViewModel(
    private val myCartFireStoreRepository: MyCartFireStoreRepository,
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository
) : ViewModel(),
    LifecycleObserver {

    private val _state = MutableStateFlow<Response<Any>>(Response.Loading)

    val state = _state.asStateFlow()

    private val _storeList = mutableStateOf<List<Store>>(emptyList())
    val storeList: State<List<Store>> = _storeList

    private val _store = mutableStateOf(Store())
    val store: State<Store> = _store


    fun fetchStoresFromFireStore() {
        viewModelScope.launch {
            try {
                val storeList = myCartFireStoreRepository.fetchAllStores()
                println("StoreList is $storeList")
                if (storeList.isNotEmpty()) {

                    _state.value = Response.SuccessList(
                        storeList,
                        DataType.STORE
                    )
                } else {
                    _state.value = Response.SuccessConfirmation("Store List Empty")
                }
            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun fetchStoreByEmailFromFireStore(email: String) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val storeInfo = myCartFireStoreRepository.fetchStoreByEmail(email)
                if (storeInfo != null) {
                    _state.value = Response.Success(storeInfo)
                }
            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun checkForAdminFromFireStore(email: String) {
        viewModelScope.launch {
            try {

                _state.value = Response.Loading
                val user = myCartFireStoreRepository.checkForAdmin(email)
                user?.let { userInfo ->
                    _state.value = Response.Success(userInfo)
                } ?: run {
                    _state.value = Response.Error("Not an Admin")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val isSignOut = myCartAuthenticationRepository.signOut()
                if (isSignOut) {
                    _state.value = Response.SignOut
                } else {
                    _state.value = Response.SuccessConfirmation("Logout Failed")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = Response.SuccessConfirmation(e.message.toString())
            }
        }
    }


}