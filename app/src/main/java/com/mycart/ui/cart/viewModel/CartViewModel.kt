package com.mycart.ui.cart.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Product
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CartViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    ViewModel(),
    LifecycleObserver  {
    private val _state = MutableStateFlow<Response<Any>>(Response.Empty)
    val state = _state.asStateFlow()

    private var _cartCount = mutableStateOf(0)
    val cartCount: State<Int> = _cartCount

    fun fetchProductListFromCart(loggedInUser: String,storeName: String){
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val productList = myCartFireStoreRepository.fetchProductListFromCart(loggedInUser,storeName)
                _state.value = Response.SuccessList(
                    productList,
                    DataType.CART
                )
                _cartCount.value = productList.size
            }catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }


    fun updateProductQuantity(loggedInUserEmail:String, product: Product, isIncrement : Boolean = false, productImage:String){
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                var existingQuantity = myCartFireStoreRepository.fetchProductQuantity(product.categoryName,product.storeName,product.productName)
                if(existingQuantity>0) {
                    val response = myCartFireStoreRepository.isProductAvailableInCart(
                        product.productName,
                        product.categoryName,
                        product.storeName,
                        loggedInUserEmail
                    )
                    when (response) {
                        is Response.Success -> {
                            if (response.data) {
                                val cartInfo = myCartFireStoreRepository.fetchCartInfo(product.productName,product.categoryName,product.storeName,
                                    loggedInUserEmail)

                                cartInfo?.let {  cartProduct ->
                                    var existingUserQuantity = cartProduct.product.userSelectedProductQty
                                    if (existingUserQuantity >= 0) {
                                        if (isIncrement) {
                                            existingQuantity -= 1
                                            existingUserQuantity += 1
                                        } else {
                                            existingQuantity += 1
                                            existingUserQuantity -= 1
                                        }
                                        val updateProductQuantityResponse = myCartFireStoreRepository.updateProductQuantity(
                                            product.productId,
                                            existingQuantity,
                                            existingUserQuantity
                                        )
                                        when (updateProductQuantityResponse) {
                                            is Response.Success -> {
                                                when(myCartFireStoreRepository.updateUserSelectedQuantity(cartProduct.cartId,existingQuantity,existingUserQuantity,loggedInUserEmail)){
                                                    is Response.Success -> {
                                                        _state.value =
                                                            Response.SuccessConfirmation("Edited Product in Cart", false)
                                                        if(existingUserQuantity == 0){
                                                            deleteProductFromCart(product,loggedInUserEmail)
                                                        }
                                                    }
                                                    is Response.Error -> {
                                                        _state.value = Response.Error("Failed User Qty update in Cart")
                                                    }
                                                    else -> {
                                                        _state.value = Response.Error("Failed User Qty update in Cart")
                                                    }
                                                }
                                            }
                                            is Response.Error -> {
                                                _state.value = Response.Error("Failed Qty update")
                                            }
                                            else -> {
                                                _state.value = Response.Error("Failed Qty update")
                                            }
                                        }
                                    }else{
                                        _state.value = Response.Error("Failed Qty update")
                                    }

                                }

                            } else {
                                val cartProduct = Product(productId = product.productId, productName = product.productName, productImage = productImage,
                                    productDiscountedPrice = product.productDiscountedPrice, productOriginalPrice = product.productOriginalPrice,
                                    productQtyUnits = product.productQtyUnits, productQty = product.productQty, userSelectedProductQty = 1,
                                    categoryName = product.categoryName, storeName = product.storeName)
                                val cart = Cart(loggedInUserEmail = loggedInUserEmail, product = cartProduct)
                                when (myCartFireStoreRepository.addProductToCart(cart)) {
                                    is Response.Success -> {
                                        _state.value = Response.SuccessConfirmation("Product Created")
                                        if (isIncrement) {
                                            existingQuantity -= 1
                                        }else{
                                            existingQuantity += 1
                                        }
                                        myCartFireStoreRepository.updateProductQuantity(
                                            product.productId,
                                            existingQuantity,
                                            0)
                                    }
                                    is Response.Error -> {
                                        _state.value = Response.Error("Error in Product Creation in Cart")
                                    }
                                    else -> {

                                    }
                                }
                            }
                        }
                        is Response.Error -> {
                            _state.value = Response.Error("Error in Product Creation in Cart")
                        }
                        else -> {
                            _state.value = Response.Error("Error in Product Creation in Cart")
                        }
                    }
                }
                else{
                    _state.value = Response.Error("Failed Qty update")
                }
            }catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }

    }

    private  fun deleteProductFromCart(product: Product,loggedUserEmail:String){
        viewModelScope.launch {
            try{
                _state.value = Response.Loading
                when (myCartFireStoreRepository.deleteProductFromCart(
                    product,loggedUserEmail
                )){
                    is Response.Success -> {
                        _state.value = Response.SuccessConfirmation("",false)
                        fetchProductListFromCart(loggedUserEmail,product.storeName)
                    }
                    is Response.Error -> {
                        _state.value = Response.Error("Error in Product Deletion from Cart")
                    }
                    else -> {

                    }
                }
            }catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

}

