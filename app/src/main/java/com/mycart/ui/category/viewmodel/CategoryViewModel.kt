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
import com.mycart.ui.common.BaseViewModel
import com.mycart.ui.common.DataType
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel(
    private val myCartAuthenticationRepository: MyCartAuthenticationRepository,
    private val myCartFireStoreRepository: MyCartFireStoreRepository
) :
    BaseViewModel(myCartAuthenticationRepository, myCartFireStoreRepository) {


    private val _categoryList = mutableStateOf<List<Category>>(emptyList())
    val categoryList: State<List<Category>> = _categoryList

    private val _dealList = mutableStateOf<List<Deal>>(emptyList())
    val dealList: State<List<Deal>> = _dealList


    val responseEvent = MutableSharedFlow<Response<Any>>()


    fun createCategory(category: Category) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val response = myCartFireStoreRepository.isCategoryAvailable(
                    category.categoryName,
                    category.storeName
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            updateState((Response.Error("Category Already Exists")))
                        } else {
                            when (myCartFireStoreRepository.createCategory(category)) {
                                is Response.Success -> {
                                    updateState((Response.SuccessConfirmation("Category Created")))
                                }
                                is Response.Error -> {
                                    updateState((Response.Error("Error in Category Creation")))
                                }
                                else -> {

                                }
                            }
                        }
                    }
                    is Response.Error -> {
                        updateState((Response.Error("Error in Category Creation")))
                    }

                    else -> {

                    }
                }


            } catch (e: Exception) {

                updateState(Response.Error("${e.message}"))
            }
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                when (myCartFireStoreRepository.deleteCategoryFromFireStore(
                    category.categoryName,
                    category.storeName
                )) {
                    is Response.Success -> {
                        fetchCategoryByStoreFromFireStore(category.storeName)
                    }
                    is Response.Error -> {
                        updateState((Response.Error("Error in Category Deletion")))
                    }
                    else -> {

                    }
                }

            } catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
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
                    updateState(Response.Success(it))

                } ?: run {
                    updateState((Response.Error("Failed to Fetch Category Info")))
                }

            } catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
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
                updateState((Response.Loading))
                val response = myCartFireStoreRepository.editCategoryInfo(
                    categoryId,
                    isDeal,
                    isSeasonal,
                    dealInfo
                )
                when (response) {
                    is Response.Success -> {
                        if (response.data) {
                            updateState(Response.SuccessConfirmation("Edited category"))
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


    fun fetchCategoryByStoreFromFireStore(storeName: String) {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val categoryList = myCartFireStoreRepository.fetchCategoryBasedOnStore(
                    storeName
                )
                updateState(
                    Response.SuccessList(
                        categoryList,
                        DataType.CATEGORY
                    )
                )

            } catch (e: Exception) {
                updateState(Response.Error("${e.message}"))
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                updateState((Response.Loading))
                val isSignOut = myCartAuthenticationRepository.signOut()
                if (isSignOut) {
                    updateState(Response.SignOut)
                } else {
                    updateState(Response.SuccessConfirmation("Logout Failed"))
                }

            } catch (e: Exception) {
                e.printStackTrace()
                updateState(Response.SuccessConfirmation(e.message.toString()))
            }
        }
    }
}


