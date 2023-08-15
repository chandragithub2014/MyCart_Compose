package com.mycart.ui.store.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Store
import com.mycart.domain.repository.MyCartRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class StoreViewModel(private val myCartRepository: MyCartRepository,private val myCartFireStoreRepository: MyCartFireStoreRepository) : ViewModel(),
    LifecycleObserver {

    private val _storeList = mutableStateOf<List<Store>>(emptyList())
    val storeList: State<List<Store>> = _storeList

    private val _store = mutableStateOf(Store())
    val store:State<Store> = _store

    val responseEvent = MutableSharedFlow<Response<Any>>()

   fun fetchStoresFromFireStore(){
       viewModelScope.launch {
           try{
               responseEvent.emit((Response.Loading))
             val storeList = myCartFireStoreRepository.fetchAllStores()
               println("StoreList is $storeList")
               if(storeList.isNotEmpty()){
                   responseEvent.emit(Response.SuccessList( storeList,
                       DataType.STORE))
               }else{
                   responseEvent.emit(Response.SuccessConfirmation("Store List Empty"))
               }
           }catch (e: Exception){
               responseEvent.emit(Response.Error("${e.message}"))
           }
       }
   }

    fun fetchStoreByEmailFromFireStore(email:String){
        viewModelScope.launch {
            try{
                responseEvent.emit((Response.Loading))
                val storeInfo = myCartFireStoreRepository.fetchStoreByEmail(email)
                if (storeInfo != null) {
                    _store.value = storeInfo
                    responseEvent.emit(Response.Success(storeInfo))
                }
            }catch (e:Exception){
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }
    fun checkForAdminFromFireStore(email: String) {
        viewModelScope.launch {
            try {
                responseEvent.emit((Response.Loading))
                val user = myCartFireStoreRepository.checkForAdmin(email)
                user?.let { userInfo ->
                    responseEvent.emit(Response.Success(userInfo))
                } ?: run {
                    responseEvent.emit(Response.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchStores() {
        viewModelScope.launch {
            try {
                val storeList = myCartRepository.fetchStores()
                if (storeList.isNotEmpty()) {
                    responseEvent.emit(
                        Response.SuccessList(
                            storeList,
                            DataType.STORE
                        )
                    )
                } else {
                    responseEvent.emit(Response.Error("No Stores found"))
                }
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchStoreByEmail(email:String){
        viewModelScope.launch {
            try{
               val storeInfo = myCartRepository.fetchStoreByEmail(email)
                if (storeInfo != null) {
                    _store.value = storeInfo
                    responseEvent.emit(Response.Success(storeInfo))
                }
            }catch (e:Exception){
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }


    fun checkForAdmin(email: String) {
        viewModelScope.launch {
            try {
                val user = myCartRepository.fetchUserInfoByEmail(email)
                user?.let { userInfo ->
                    responseEvent.emit(Response.Success(userInfo))
                } ?: run {
                    responseEvent.emit(Response.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}