package com.mycart.ui.category.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.Category
import com.mycart.domain.model.Deal
import com.mycart.domain.repository.MyCartRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class CategoryViewModel(private val myCartRepository: MyCartRepository) : ViewModel() ,LifecycleObserver{
    private val _categoryList = mutableStateOf<List<Category>>(emptyList())
    val categoryList: State<List<Category>> = _categoryList

    private val _dealList = mutableStateOf<List<Deal>>(emptyList())
    val dealList:State<List<Deal>> = _dealList

    fun fetchCategories(){
        viewModelScope.launch {
            try{
              val categoryResponse = myCartRepository.fetchCategoryDetails()
                _categoryList.value = categoryResponse
            }
            catch (e:Exception){

            }
        }
    }

    fun fetchSeasonalCategories(){
        viewModelScope.launch {
            try{
                val categoryResponse = myCartRepository.fetchSeasonalCategoryDetails()
                _categoryList.value = categoryResponse
            }
            catch (e:Exception){

            }
        }
    }

    fun fetchDeals(){
        viewModelScope.launch {
            try {
                val dealResponse = myCartRepository.fetchDeals()
                _dealList.value = dealResponse

            } catch (e: Exception) {

            }
        }
    }

}