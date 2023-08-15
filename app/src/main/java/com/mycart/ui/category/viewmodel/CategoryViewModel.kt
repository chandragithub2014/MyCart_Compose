package com.mycart.ui.category.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.model.User
import com.mycart.domain.repository.MyCartRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel(private val myCartRepository: MyCartRepository,private val myCartFireStoreRepository: MyCartFireStoreRepository) : ViewModel(),
    LifecycleObserver {
    private val _categoryList = mutableStateOf<List<Category>>(emptyList())
    val categoryList: State<List<Category>> = _categoryList

    private val _dealList = mutableStateOf<List<Deal>>(emptyList())
    val dealList: State<List<Deal>> = _dealList

    fun fetchCategories() {
        viewModelScope.launch {
            try {
                val categoryResponse = myCartRepository.fetchCategoryDetails()
                _categoryList.value = categoryResponse
            } catch (e: Exception) {

            }
        }
    }

    fun fetchSeasonalCategories() {
        viewModelScope.launch {
            try {
                val categoryResponse = myCartRepository.fetchSeasonalCategoryDetails()
                _categoryList.value = categoryResponse
            } catch (e: Exception) {

            }
        }
    }

    fun fetchDeals() {
        viewModelScope.launch {
            try {
                val dealResponse = myCartRepository.fetchDeals()
                _dealList.value = dealResponse

            } catch (e: Exception) {

            }
        }
    }

    val responseEvent = MutableSharedFlow<Response<Any>>()
    private var _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState

    fun checkForAdminFromFireStore(email: String) {
        viewModelScope.launch {
            try {
                responseEvent.emit((Response.Loading))
                val user = myCartFireStoreRepository.checkForAdmin(email)
                user?.let { userInfo ->
                    _isAdminState.value = userInfo.admin
                    responseEvent.emit(Response.Success(userInfo))
                } ?: run {
                    responseEvent.emit(Response.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun checkForAdmin(email: String) {
        viewModelScope.launch {
            try {
                val user = myCartRepository.fetchUserInfoByEmail(email)
                user?.let { userInfo ->
                    _isAdminState.value = userInfo.admin
                    responseEvent.emit(Response.Success(userInfo))
                } ?: run {
                    responseEvent.emit(Response.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun createCategory(category: Category) {
        viewModelScope.launch {
            try {
                val isCategoryExists = myCartRepository.isCategoryAvailable(category.categoryName,category.storeName)
                if (isCategoryExists) {
                    responseEvent.emit(Response.Error("Category Already Exists"))
                } else {
                    val insertedId = myCartRepository.createCategory(category)
                    if (insertedId > 0) {
                        responseEvent.emit(Response.SuccessConfirmation("Category Created"))
                    } else {
                        responseEvent.emit(Response.Error("Category Creation failed"))
                    }
                }
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchCategoryForAdmin(user: User) {
        viewModelScope.launch {
            try {
                val categoryList = myCartRepository.fetchAllCategories(
                    user.userStoreLocation,
                    user.userStore,
                    user.userEmail
                )
                if (categoryList.isNotEmpty()) {
                    responseEvent.emit(
                        Response.SuccessList(
                            categoryList,
                            DataType.CATEGORY
                        )
                    )
                } else {
                    responseEvent.emit(Response.Error("No Categories found"))
                }
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchDealsForAdmin(user: User) {
        viewModelScope.launch {
            try {
                val dealList = myCartRepository.fetchAllDeals(
                    user.userStoreLocation,
                    user.userStore,
                    user.userEmail
                )
                if (dealList.isNotEmpty()) {
                    responseEvent.emit(
                        Response.SuccessList(
                            dealList,
                            DataType.DEALS
                        )
                    )
                } else {
                    responseEvent.emit(Response.Error("No Deals found"))
                }
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchSeasonalDealsForAdmin(user: User) {
        viewModelScope.launch {
            try {
                val dealList = myCartRepository.fetchSeasonalDeals(
                    user.userStoreLocation,
                    user.userStore,
                    user.userEmail
                )
                if (dealList.isNotEmpty()) {
                    responseEvent.emit(
                        Response.SuccessList(
                            dealList,
                            DataType.SEASONALDEALS
                        )
                    )
                } else {
                    responseEvent.emit(Response.Error("No seasonal specials found"))
                }
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }


    fun fetchCategoryByStore(storeName: String) {
        viewModelScope.launch {
            try {
                val categoryList = myCartRepository.fetchCategoriesByStore(
                    storeName
                )

                responseEvent.emit(
                    Response.SuccessList(
                        categoryList,
                        DataType.CATEGORY
                    )
                )
            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchDealsByStore(storeName: String) {
        viewModelScope.launch {
            try {
                val dealList = myCartRepository.fetchDealsByStore(
                    storeName
                )
                responseEvent.emit(
                    Response.SuccessList(
                        dealList,
                        DataType.DEALS
                    )
                )

            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchSeasonalDealsByStore(storeName: String) {
        viewModelScope.launch {
            try {
                val dealList = myCartRepository.fetchSeasonalDetalsByStore(
                    storeName
                )
                responseEvent.emit(
                    Response.SuccessList(
                        dealList,
                        DataType.SEASONALDEALS
                    )
                )

            } catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun deleteSelectedCategory(category: Category){
        viewModelScope.launch {
            try{
               val deletedRows = myCartRepository.deleteCategoryByStore(category.categoryName,category.storeName)
                if(deletedRows > 0){
                    fetchCategoryByStore(category.storeName)
                    fetchDealsByStore(category.storeName)
                    fetchSeasonalDealsByStore(category.storeName)
                }else{
                    responseEvent.emit(Response.Error("No Rows Deleted"))
                }
            }catch (e: Exception) {
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun fetchCategoryInfoByCategoryNameAndStoreNumber(categoryName:String,storeName:String){
        viewModelScope.launch {
            try{
                val receivedCategory = myCartRepository.fetchCategoryInfo(categoryName,storeName)
                receivedCategory?.let {
                    responseEvent.emit(Response.Success(it))
                }?:run{
                    responseEvent.emit(Response.Error("Failed to Fetch Category Info"))
                }

            }catch (e:Exception){
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }

    fun updateCategory(categoryName:String,storeName:String,isDeal:Boolean,isSeasonal:Boolean,dealInfo:String){
        viewModelScope.launch {
            try{
                val updatedRows = myCartRepository.editCategoryInfo(categoryName,storeName,isDeal,isSeasonal,dealInfo)
                if(updatedRows > 0) {
                    responseEvent.emit(Response.SuccessConfirmation("Edited category"))
                }else{
                    responseEvent.emit(Response.Error("No Rows Updated"))
                }
            }catch (e:Exception){
                responseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }
}


