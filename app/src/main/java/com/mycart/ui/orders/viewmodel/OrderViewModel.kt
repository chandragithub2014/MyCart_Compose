package com.mycart.ui.orders.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.BaseViewModel
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.launch
import java.lang.Exception

class OrderViewModel(private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
                     private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    BaseViewModel(myCartAuthenticationRepository, myCartFireStoreRepository),
    LifecycleObserver  {

    fun fetchOrderListByLoggedInUser(userEmail: String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val orderList = myCartFireStoreRepository.fetchOrderList(email = userEmail)
                updateState((Response.SuccessList(
                    orderList,
                    DataType.ORDER
                )))
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun fetchOrderListByStore(store:String){
        viewModelScope.launch {
            try{
                updateState((Response.Loading))
                val orderList = myCartFireStoreRepository.fetchOrderListByStore(store=store)
                updateState((Response.SuccessList(
                    orderList,
                    DataType.ORDER
                )))
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

}