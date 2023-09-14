package com.mycart.ui.cart.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Order
import com.mycart.domain.model.OrderDetail
import com.mycart.domain.model.Product
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.BaseViewModel
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import com.mycart.ui.utils.getCurrentDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CartViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    BaseViewModel(myCartAuthenticationRepository, myCartFireStoreRepository),
    LifecycleObserver {



    fun performCheckout(loggedInUser:String,storeName:String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val productList = myCartFireStoreRepository.fetchProductListFromCart(loggedInUser,storeName)
                if(productList.isNotEmpty()){
                  val order =   Order(loggedInUserEmail = loggedInUser, store = storeName, orderedDateTime = getCurrentDateTime())
                  val orderId = order.orderId
                  for(cartProduct in productList){
                      val orderDetail = OrderDetail(orderId = orderId, loggedInUserEmail = loggedInUser, product = cartProduct.product)
                      createOrderDetails(orderDetail)
                  }
                  createOrder(order)
                }
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }
    
    private fun createOrder(order: Order) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.createOrder(order)) {
                    is Response.Success -> {
                        updateState((Response.SuccessConfirmation("Order Created")))
                        deleteCartInfoForLoggedInUser(order.loggedInUserEmail,order.store)
                    }
                    is Response.Error -> {
                        updateState((Response.Error("Error in Order Creation")))
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    private fun createOrderDetails(orderDetail: OrderDetail) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.createOrderDetails(orderDetail)) {
                    is Response.Error -> {
                        updateState((Response.Error("Error in OrderDetail Creation")))
                    }
                    else -> {

                    }
                }
            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }


    private fun deleteCartInfoForLoggedInUser(loggedInUser:String,storeName:String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.deleteCartInfoBasedOnLoggedUser(
                    loggedInUser, storeName
                )) {
                    is Response.Success -> {
                        updateState((Response.Refresh))

                    }
                    is Response.Error -> {
                        updateState(Response.Error("Error in Product Deletion from Cart"))
                    }
                    else -> {

                    }
                }
            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

}

