package com.mycart.ui.product.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class ProductViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    ViewModel(),
    LifecycleObserver {

    private val _state = MutableStateFlow<Response<Any>>(Response.Empty)
    val state = _state.asStateFlow()

    private val _productList = mutableStateOf<List<Product>>(emptyList())

    private var _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState


    fun checkForAdminFromFireStore(email: String) {
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


    fun createProduct(product: Product) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val response = myCartFireStoreRepository.isProductAvailable(
                    product.productName,
                    product.categoryName,
                    product.storeName
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            _state.value = Response.Error("Product Already Exists")
                        } else {
                            when (myCartFireStoreRepository.createProduct(product)) {
                                is Response.Success -> {
                                    _state.value = Response.SuccessConfirmation("Product Created")
                                }
                                is Response.Error -> {
                                    _state.value = Response.Error("Error in Product Creation")
                                }
                                else -> {

                                }
                            }
                        }
                    }
                    is Response.Error -> {
                        _state.value = Response.Error("Error in Product Creation")
                    }

                    else -> {

                    }
                }


            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }
}