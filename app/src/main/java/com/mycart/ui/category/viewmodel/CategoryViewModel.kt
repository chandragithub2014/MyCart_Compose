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
import com.mycart.ui.common.DataType
import com.mycart.ui.common.ValidationState
import com.mycart.ui.register.viewmodel.FormValidationResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel(private val myCartRepository: MyCartRepository) : ViewModel(),
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

    val validationEvent = MutableSharedFlow<ValidationState<Any>>()
    private var _isAdminState = mutableStateOf(false)
    val isAdminState: State<Boolean> = _isAdminState
    fun checkForAdmin(email: String) {
        viewModelScope.launch {
            try {
                val user = myCartRepository.fetchUserInfoByEmail(email)
                user?.let { userInfo ->
                    _isAdminState.value = userInfo.isAdmin
                    validationEvent.emit(ValidationState.Success(userInfo))
                } ?: run {
                    validationEvent.emit(ValidationState.Error("Not an Admin"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun createCategory(category: Category) {
        viewModelScope.launch {
            try {
                val isCategoryExists = myCartRepository.isCategoryAvailable(category.categoryName)
                if (isCategoryExists) {
                    validationEvent.emit(ValidationState.Error("Category Already Exists"))
                } else {
                    val insertedId = myCartRepository.createCategory(category)
                    if (insertedId > 0) {
                        validationEvent.emit(ValidationState.SuccessConfirmation("Category Created"))
                    } else {
                        validationEvent.emit(ValidationState.Error("Category Creation failed"))
                    }
                }
            } catch (e: Exception) {
                validationEvent.emit(ValidationState.Error("${e.message}"))
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
                    validationEvent.emit(
                        ValidationState.SuccessList(
                            categoryList,
                            DataType.CATEGORY
                        )
                    )
                } else {
                    validationEvent.emit(ValidationState.Error("No Categories found"))
                }
            } catch (e: Exception) {
                validationEvent.emit(ValidationState.Error("${e.message}"))
            }
        }
    }

    fun fetchDealsForAdmin(user:User){
        viewModelScope.launch {
         try{
             val dealList = myCartRepository.fetchAllDeals(
                 user.userStoreLocation,
                 user.userStore,
                 user.userEmail
             )
             if (dealList.isNotEmpty()) {
                 validationEvent.emit(
                     ValidationState.SuccessList(
                         dealList,
                         DataType.DEALS
                     )
                 )
             } else {
                 validationEvent.emit(ValidationState.Error("No Deals found"))
             }
         }catch (e:Exception){
             validationEvent.emit(ValidationState.Error("${e.message}"))
         }
        }
    }

    fun fetchSeasonalDealsForAdmin(user:User){
        viewModelScope.launch {
            try{
                val dealList = myCartRepository.fetchSeasonalDeals(
                    user.userStoreLocation,
                    user.userStore,
                    user.userEmail
                )
                if (dealList.isNotEmpty()) {
                    validationEvent.emit(
                        ValidationState.SuccessList(
                            dealList,
                            DataType.SEASONALDEALS
                        )
                    )
                } else {
                    validationEvent.emit(ValidationState.Error("No seasonal specials found"))
                }
            }catch (e:Exception){
                validationEvent.emit(ValidationState.Error("${e.message}"))
            }
        }
    }
}


