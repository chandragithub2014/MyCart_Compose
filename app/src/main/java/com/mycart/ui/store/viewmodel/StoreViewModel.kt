package com.mycart.ui.store.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Store
import com.mycart.domain.repository.MyCartRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.ValidationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class StoreViewModel(private val myCartRepository: MyCartRepository) : ViewModel(),
    LifecycleObserver {

    private val _storeList = mutableStateOf<List<Store>>(emptyList())
    val storeList: State<List<Store>> = _storeList

    private val _store = mutableStateOf(Store())
    val store:State<Store> = _store

    val validationEvent = MutableSharedFlow<ValidationState<Any>>()

    fun fetchStores() {
        viewModelScope.launch {
            try {
                val storeList = myCartRepository.fetchStores()
                if (storeList.isNotEmpty()) {
                    validationEvent.emit(
                        ValidationState.SuccessList(
                            storeList,
                            DataType.STORE
                        )
                    )
                } else {
                    validationEvent.emit(ValidationState.Error("No Stores found"))
                }
            } catch (e: Exception) {
                validationEvent.emit(ValidationState.Error("${e.message}"))
            }
        }
    }

    fun fetchStoreByEmail(email:String){
        viewModelScope.launch {
            try{
               val storeInfo = myCartRepository.fetchStoreByEmail(email)
                if (storeInfo != null) {
                    _store.value = storeInfo
                    validationEvent.emit(ValidationState.Success(storeInfo))
                }
            }catch (e:Exception){
                validationEvent.emit(ValidationState.Error("${e.message}"))
            }
        }
    }


    fun checkForAdmin(email: String) {
        viewModelScope.launch {
            try {
                val user = myCartRepository.fetchUserInfoByEmail(email)
                user?.let { userInfo ->
                    validationEvent.emit(ValidationState.Success(userInfo))
                } ?: run {
                    validationEvent.emit(ValidationState.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}