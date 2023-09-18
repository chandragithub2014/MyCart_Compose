package com.mycart.ui.orderDetail.viewModel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.BaseViewModel
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.launch
import java.lang.Exception

class OrderDetailViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    BaseViewModel(myCartAuthenticationRepository, myCartFireStoreRepository),
    LifecycleObserver {


    fun fetchOrderListByOrderIdLoggedInUser(userEmail: String,orderId:String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val orderList = myCartFireStoreRepository.fetchOrderDetailList(email = userEmail,orderID= orderId)
                updateState((Response.SuccessList(
                    orderList,
                    DataType.ORDER_DETAIL
                )))
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

}