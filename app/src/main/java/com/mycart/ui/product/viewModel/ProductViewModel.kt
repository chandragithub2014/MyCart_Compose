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

    fun fetchCategoryInfoByCategoryNameAndStoreNumber(categoryName: String, storeName: String) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val receivedCategory =
                    myCartFireStoreRepository.fetchCategoryInfo(categoryName, storeName)
                receivedCategory?.let {
                    _state.value = Response.Success(it)

                } ?: run {
                    _state.value = Response.Error("Failed to Fetch Category Info")
                }

            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun fetchProductListByCategoryAndStoreNumber(categoryName: String,storeName: String){
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val productList = myCartFireStoreRepository.fetchProductsByCategoryAndStore(categoryName = categoryName,storeName)
                _state.value = Response.SuccessList(
                    productList,
                    DataType.PRODUCT
                )
            }catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val isSignOut = myCartAuthenticationRepository.signOut()
                if (isSignOut) {
                    _state.value = Response.SignOut
                } else {
                    _state.value = Response.SuccessConfirmation("Logout Failed")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _state.value = Response.SuccessConfirmation(e.message.toString())
            }
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                when (myCartFireStoreRepository.deleteProduct(
                    product.categoryName,
                    product.storeName,
                    product.productName
                )) {
                    is Response.Success -> {
                        fetchProductListByCategoryAndStoreNumber(product.categoryName,product.storeName)
                    }
                    is Response.Error -> {
                        _state.value = Response.Error("Error in Product Deletion")
                    }
                    else -> {

                    }
                }

            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    private var _selectedQtyIndex = mutableStateOf(-1)
    val selectedQtyIndex: State<Int> = _selectedQtyIndex

    private var _selectedQtyUnitIndex = mutableStateOf(-1)
    val selectedQtyUnitIndex: State<Int> = _selectedQtyUnitIndex


    fun fetchProductInfoByCategoryStore(categoryName: String,storeName: String,productName:String){
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val receivedCategory =
                    myCartFireStoreRepository.fetchProductInfo(categoryName,storeName,productName)
                receivedCategory?.let { selectedProduct ->
                    _selectedQtyIndex.value = ProductUtils.fetchProductQty().indexOf(selectedProduct.productQty.toString()).takeIf { it != -1 } ?: 0
                    _selectedQtyUnitIndex.value = ProductUtils.fetchProductQtyInUnits().indexOf(selectedProduct.productQtyUnits).takeIf { it != -1 } ?:0
                    _state.value = Response.Success(selectedProduct)
                } ?: run {
                    _state.value = Response.Error("Failed to Fetch Product Info")
                }

            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }


    fun updateSelectedProduct(
      product: Product
    ) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val response = myCartFireStoreRepository.editProductInfo(
                    product
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            _state.value = Response.SuccessConfirmation("Edited Product")
                        } else {
                            _state.value = Response.Error("No Rows Updated")
                        }
                    }

                    is Response.Error -> {
                        _state.value = Response.Error("No Rows Updated")
                    }
                    else -> {
                        _state.value = Response.Error("No Rows Updated")
                    }
                }

            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun updateProductQuantity(loggedInUserEmail:String,product: Product,isIncrement : Boolean = false){
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
                                val cartProduct = Product(productId = product.productId, productName = product.productName, productImage = product.productImage,
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

