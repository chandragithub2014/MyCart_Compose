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

    fun fetchOrderListByOrderId(orderId:String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val orderList = myCartFireStoreRepository.fetchOrderDetailListByOrderId(orderID= orderId)
                updateState((Response.SuccessList(
                    orderList,
                    DataType.ORDER_DETAIL
                )))
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun fetchOrderInfoByOrderId(orderId: String) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val selectedOrder = myCartFireStoreRepository.fetchOrderInfo(orderId)
                selectedOrder?.let { orderInfo ->
                    updateState((Response.Success(orderInfo)))
                } ?: run {
                    updateState((Response.Error("Failed to Fetch Order Info")))
                }
            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun updateOrder(
        orderId: String,
        orderStatus: String,
        additionalInfo: String = ""
    ) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val response = myCartFireStoreRepository.updateOrderStatus(
                    orderId,
                    additionalInfo,
                    orderStatus
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            updateState(Response.SuccessConfirmation("Updated  Order"))
                        } else {
                            updateState(Response.Error("No Rows Updated"))
                        }
                    }

                    is Response.Error -> {
                        updateState(Response.Error("No Rows Updated"))
                    }
                    else -> {
                        updateState(Response.Error("No Rows Updated"))
                    }
                }

            } catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
            }
        }
    }

}