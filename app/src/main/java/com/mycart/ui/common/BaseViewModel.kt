package com.mycart.ui.common

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Product
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.log

open class BaseViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) : ViewModel(), LifecycleObserver {

    private var _state = MutableStateFlow<Response<Any>>(Response.Empty)
    val state = _state.asStateFlow()

    private var _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState


    private var _refreshCart = mutableStateOf(false)
    val refreshCart: State<Boolean> = _refreshCart

    private var _userSelectedQuantity = mutableStateOf(0)
    val userSelectedQuantity :State<Int> = _userSelectedQuantity

    private var _cartProductList = mutableStateOf(listOf<Cart>())
    val cartProductList:State<List<Cart>> = _cartProductList

    private var _maxProductAddCountWarning = mutableStateOf(0)
    val maxProductAddCountWarning:State<Int> = _maxProductAddCountWarning

    fun checkForAdmin(email: String) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val user = myCartFireStoreRepository.checkForAdmin(email)
                user?.let { userInfo ->
                    _isAdminState.value = userInfo.admin
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

    fun updateProductQuantity(
        loggedInUserEmail: String,
        product: Product,
        isIncrement: Boolean = false,
        productImage: String
    ) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                var existingQuantity = myCartFireStoreRepository.fetchProductQuantity(
                    product.categoryName,
                    product.storeName,
                    product.productName
                )
                if (existingQuantity > 0) {
                    val response = myCartFireStoreRepository.isProductAvailableInCart(
                        product.productName,
                        product.categoryName,
                        product.storeName,
                        loggedInUserEmail
                    )
                    when (response) {
                        is Response.Success -> {
                            if (response.data) {
                                val cartInfo = myCartFireStoreRepository.fetchCartInfo(
                                    product.productName, product.categoryName, product.storeName,
                                    loggedInUserEmail
                                )

                                cartInfo?.let { cartProduct ->
                                    var existingUserQuantity =
                                        cartProduct.product.userSelectedProductQty
                                    if (existingUserQuantity >= 0) {
                                        if (isIncrement) {
                                            existingQuantity -= 1
                                            existingUserQuantity += 1
                                            if(existingUserQuantity >=4){
                                                existingUserQuantity = 3
                                                _maxProductAddCountWarning.value = existingUserQuantity
                                            }else{
                                                _maxProductAddCountWarning.value = 0
                                            }
                                        } else {
                                            existingQuantity += 1
                                            existingUserQuantity -= 1
                                            if(existingUserQuantity < 3){
                                                _maxProductAddCountWarning.value = 0
                                            }
                                        }
                                        val updateProductQuantityResponse =
                                            myCartFireStoreRepository.updateProductQuantity(
                                                product.productId,
                                                existingQuantity,
                                                existingUserQuantity
                                            )
                                        when (updateProductQuantityResponse) {
                                            is Response.Success -> {
                                                when (myCartFireStoreRepository.updateUserSelectedQuantity(
                                                    cartProduct.cartId,
                                                    existingQuantity,
                                                    existingUserQuantity,
                                                    loggedInUserEmail
                                                )) {
                                                    is Response.Success -> {
                                                        /*   _state.value =
                                                               Response.SuccessConfirmation("Edited Product in Cart", false)*/
                                                        _userSelectedQuantity.value = existingUserQuantity
                                                        updateState(
                                                            (Response.SuccessConfirmation(
                                                                "Edited Product in Cart",
                                                                false
                                                            ))
                                                        )
                                                        if (existingUserQuantity == 0) {
                                                            deleteProductFromCart(
                                                                product,
                                                                loggedInUserEmail
                                                            )
                                                        }
                                                    }
                                                    is Response.Error -> {
                                                        //   _state.value = Response.Error("Failed User Qty update in Cart")
                                                        updateState((Response.Error("Failed User Qty update in Cart")))
                                                    }
                                                    else -> {
                                                        // _state.value = Response.Error("Failed User Qty update in Cart")
                                                        updateState((Response.Error("Failed User Qty update in Cart")))
                                                    }
                                                }
                                            }
                                            is Response.Error -> {
                                                //  _state.value = Response.Error("Failed Qty update")
                                                updateState((Response.Error("Failed Qty update")))
                                            }
                                            else -> {
                                                //    _state.value = Response.Error("Failed Qty update")
                                                updateState((Response.Error("Failed Qty update")))
                                            }
                                        }
                                    } else {
                                        //  _state.value = Response.Error("Failed Qty update")
                                        updateState((Response.Error("Failed Qty update")))
                                    }

                                }

                            } else {
                                val cartProduct = Product(
                                    productId = product.productId,
                                    productName = product.productName,
                                    productImage = productImage,
                                    productDiscountedPrice = product.productDiscountedPrice,
                                    productOriginalPrice = product.productOriginalPrice,
                                    productQtyUnits = product.productQtyUnits,
                                    productQty = product.productQty,
                                    userSelectedProductQty = 1,
                                    categoryName = product.categoryName,
                                    storeName = product.storeName
                                )
                                val cart = Cart(
                                    loggedInUserEmail = loggedInUserEmail,
                                    product = cartProduct
                                )
                                when (myCartFireStoreRepository.addProductToCart(cart)) {
                                    is Response.Success -> {
                                        // _state.value = Response.SuccessConfirmation("Product Created")
                                        updateState((Response.SuccessConfirmation("Product Created")))
                                        if (isIncrement) {
                                            existingQuantity -= 1
                                        } else {
                                            existingQuantity += 1
                                        }
                                        myCartFireStoreRepository.updateProductQuantity(
                                            product.productId,
                                            existingQuantity,
                                            0
                                        )
                                    }
                                    is Response.Error -> {
                                        //   _state.value = Response.Error("Error in Product Creation in Cart")
                                        updateState((Response.Error("Error in Product Creation in Cart")))

                                    }
                                    else -> {

                                    }
                                }
                            }
                        }
                        is Response.Error -> {
                            // _state.value = Response.Error("Error in Product Creation in Cart")
                            updateState((Response.Error("Error in Product Creation in Cart")))
                        }
                        else -> {
                            //  _state.value = Response.Error("Error in Product Creation in Cart")
                            updateState((Response.Error("Error in Product Creation in Cart")))
                        }
                    }
                } else {
                    //     _state.value = Response.Error("Failed Qty update")
                    updateState((Response.Error("Failed Qty update")))
                }
            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
            fetchProductListFromCart(loggedInUserEmail,product.storeName)
            calculateCost(loggedInUserEmail,product.storeName)
        }
    }

    private fun deleteProductFromCart(product: Product, loggedUserEmail: String) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.deleteProductFromCart(
                    product, loggedUserEmail
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
            } catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
            }
        }
    }

    private var _cartCount = mutableStateOf(0)
    val cartCount: State<Int> = _cartCount



    fun fetchProductListFromCart(loggedInUser: String,storeName: String){
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val productList = myCartFireStoreRepository.fetchProductListFromCart(loggedInUser,storeName)
                updateState(Response.SuccessList(
                    productList,
                    DataType.CART
                ))
                _cartCount.value = productList.size
            }catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
            }
        }
    }

    private var _totalCost = mutableStateOf(0)
    val totalCost: State<Int> = _totalCost

    fun calculateCost(loggedInUser: String,storeName: String){
        var cartCost = 0
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val productList = myCartFireStoreRepository.fetchProductListFromCart(loggedInUser,storeName)
                if(productList.isNotEmpty()){
                    for(cartProduct in productList) {
                        cartCost += (cartProduct.product.userSelectedProductQty) * (cartProduct.product.productDiscountedPrice).toInt()
                    }
                    _totalCost.value = cartCost
                }

            }catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun updateState(response: Response<Any>) {
        _state.value = response
    }
}