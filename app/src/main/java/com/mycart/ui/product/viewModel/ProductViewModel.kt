package com.mycart.ui.product.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Cart
import com.mycart.domain.model.Category
import com.mycart.domain.model.Product
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.category.utils.CategoryUtils
import com.mycart.ui.common.BaseViewModel
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import com.mycart.ui.product.utils.ProductUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.math.log


class ProductViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    BaseViewModel(myCartAuthenticationRepository, myCartFireStoreRepository) {


    fun createProduct(product: Product) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val response = myCartFireStoreRepository.isProductAvailable(
                    product.productName,
                    product.categoryName,
                    product.storeName
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            updateState((Response.Error("Product Already Exists")))

                        } else {
                            when (myCartFireStoreRepository.createProduct(product)) {
                                is Response.Success -> {
                                    updateState((Response.SuccessConfirmation("Product Created")))

                                }
                                is Response.Error -> {
                                    //    _state.value = Response.Error("Error in Product Creation")
                                    updateState((Response.Error("Error in Product Creation")))
                                }
                                else -> {

                                }
                            }
                        }
                    }
                    is Response.Error -> {
                        //  _state.value = Response.Error("Error in Product Creation")
                        updateState((Response.Error("Error in Product Creation")))
                    }

                    else -> {

                    }
                }


            } catch (e: Exception) {
                //     _state.value = Response.Error("${e.message}")
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun fetchCategoryInfoByCategoryNameAndStoreNumber(categoryName: String, storeName: String) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val receivedCategory =
                    myCartFireStoreRepository.fetchCategoryInfo(categoryName, storeName)
                receivedCategory?.let {
                    //    _state.value = Response.Success(it)
                    updateState((Response.Success(it)))

                } ?: run {
                    //     _state.value = Response.Error("Failed to Fetch Category Info")
                    updateState((Response.Error("Failed to Fetch Category Info")))
                }

            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun fetchProductListByCategoryAndStoreNumber(categoryName: String, storeName: String) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val productList = myCartFireStoreRepository.fetchProductsByCategoryAndStore(
                    categoryName = categoryName,
                    storeName
                )
                /* _state.value = Response.SuccessList(
                     productList,
                     DataType.PRODUCT
                 )*/
                updateState(
                    (Response.SuccessList(
                        productList,
                        DataType.PRODUCT
                    ))
                )
            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val isSignOut = myCartAuthenticationRepository.signOut()
                if (isSignOut) {
                    // _state.value = Response.SignOut
                    updateState((Response.SignOut))
                } else {
                    //  _state.value = Response.SuccessConfirmation("Logout Failed")
                    updateState((Response.SuccessConfirmation("Logout Failed")))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                //  _state.value = Response.SuccessConfirmation(e.message.toString())
                updateState((Response.SuccessConfirmation(e.message.toString())))
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.deleteProduct(
                    product.categoryName,
                    product.storeName,
                    product.productName
                )) {
                    is Response.Success -> {
                        fetchProductListByCategoryAndStoreNumber(
                            product.categoryName,
                            product.storeName
                        )
                    }
                    is Response.Error -> {
                        //    _state.value = Response.Error("Error in Product Deletion")
                        updateState((Response.Error("Error in Product Deletion")))
                    }
                    else -> {

                    }
                }

            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }

    private var _selectedQtyIndex = mutableStateOf(-1)
    val selectedQtyIndex: State<Int> = _selectedQtyIndex

    private var _selectedQtyUnitIndex = mutableStateOf(-1)
    val selectedQtyUnitIndex: State<Int> = _selectedQtyUnitIndex


    fun fetchProductInfoByCategoryStore(
        categoryName: String,
        storeName: String,
        productName: String
    ) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val receivedCategory =
                    myCartFireStoreRepository.fetchProductInfo(categoryName, storeName, productName)
                receivedCategory?.let { selectedProduct ->
                    _selectedQtyIndex.value = ProductUtils.fetchProductQty()
                        .indexOf(selectedProduct.productQty.toString()).takeIf { it != -1 } ?: 0
                    _selectedQtyUnitIndex.value = ProductUtils.fetchProductQtyInUnits()
                        .indexOf(selectedProduct.productQtyUnits).takeIf { it != -1 } ?: 0
                    //  _state.value = Response.Success(selectedProduct)
                    updateState((Response.Success(selectedProduct)))
                } ?: run {
                    //   _state.value = Response.Error("Failed to Fetch Product Info")
                    updateState((Response.Error("Failed to Fetch Product Info")))
                }

            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }


    fun updateSelectedProduct(
        product: Product
    ) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val response = myCartFireStoreRepository.editProductInfo(
                    product
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            updateState((Response.SuccessConfirmation("Edited Product")))
                            //  _state.value = Response.SuccessConfirmation("Edited Product")
                        } else {
                            //   _state.value = Response.Error("No Rows Updated")
                            updateState((Response.Error("No Rows Updated")))
                        }
                    }

                    is Response.Error -> {
                        //  _state.value = Response.Error("No Rows Updated")
                        updateState((Response.Error("No Rows Updated")))
                    }
                    else -> {
                        // _state.value = Response.Error("No Rows Updated")
                        updateState((Response.Error("No Rows Updated")))
                    }
                }

            } catch (e: Exception) {
                updateState((Response.Error("${e.message}")))
            }
        }
    }


}

