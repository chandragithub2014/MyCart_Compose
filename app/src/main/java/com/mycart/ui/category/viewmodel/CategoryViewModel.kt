package com.mycart.ui.category.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel(private val myCartAuthenticationRepository: MyCartAuthenticationRepository,private val myCartFireStoreRepository: MyCartFireStoreRepository) :
    ViewModel(),
    LifecycleObserver {

    private val _state = MutableStateFlow<Response<Any>>(Response.Loading)
    val state = _state.asStateFlow()

    private val _categoryList = mutableStateOf<List<Category>>(emptyList())
    val categoryList: State<List<Category>> = _categoryList

    private val _dealList = mutableStateOf<List<Deal>>(emptyList())
    val dealList: State<List<Deal>> = _dealList


    val responseEvent = MutableSharedFlow<Response<Any>>()

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


    fun createCategory(category: Category) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val response = myCartFireStoreRepository.isCategoryAvailable(
                    category.categoryName,
                    category.storeName
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            _state.value = Response.Error("Category Already Exists")
                        } else {
                            when (myCartFireStoreRepository.createCategory(category)) {
                                is Response.Success -> {
                                    _state.value = Response.SuccessConfirmation("Category Created")
                                }
                                is Response.Error -> {
                                    _state.value = Response.Error("Error in Category Creation")
                                }
                                else -> {

                                }
                            }
                        }
                    }
                    is Response.Error -> {
                        _state.value = Response.Error("Error in Category Creation")
                    }

                    else -> {

                    }
                }


            } catch (e: Exception) {
                _state.value = Response.Error("${e.message}")
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                when (myCartFireStoreRepository.deleteCategoryFromFireStore(
                    category.categoryName,
                    category.storeName
                )) {
                    is Response.Success -> {
                        fetchCategoryByStoreFromFireStore(category.storeName)
                    }
                    is Response.Error -> {
                        _state.value = Response.Error("Error in Category Deletion")
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


    fun updateCategoryInFireStore(
        categoryId: String,
        isDeal: Boolean,
        isSeasonal: Boolean,
        dealInfo: String
    ) {
        viewModelScope.launch {
            try {
                _state.value = Response.Loading
                val response = myCartFireStoreRepository.editCategoryInfo(
                    categoryId,
                    isDeal,
                    isSeasonal,
                    dealInfo
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            _state.value = Response.SuccessConfirmation("Edited category")
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


    fun fetchCategoryByStoreFromFireStore(storeName: String) {
        viewModelScope.launch {
            try {
                val categoryList = myCartFireStoreRepository.fetchCategoryBasedOnStore(
                    storeName
                )
                _state.value = Response.SuccessList(
                    categoryList,
                    DataType.CATEGORY
                )

            } catch (e: Exception) {
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
}


