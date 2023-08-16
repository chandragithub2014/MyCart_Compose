package com.mycart.ui.launcher.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.MyCartRepository
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.domain.repository.firebase.MyCartFireStoreRepository
import com.mycart.ui.common.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class AppLauncherViewModel(private val myCartAuthenticationRepository: MyCartAuthenticationRepository) :
    ViewModel(),
    LifecycleObserver {

    private val _state = MutableStateFlow<Response<Any>>(Response.Loading)

    val state = _state.asStateFlow()

    fun checkUser(){
        viewModelScope.launch {
            try{
                _state.value = Response.Loading
                val result = myCartAuthenticationRepository.isUserLoggedIn()
                result?.let {
                    val receivedUser = myCartAuthenticationRepository.getCurrentUser()
                    receivedUser.collect{ user ->
                        user?.let {
                            _state.value =      Response.Success(it)
                        }?: run {
                            _state.value =     Response.Login
                        }
                    }
                }?:run{
                    _state.value = Response.Login
                }

            }catch (e:Exception){
                e.printStackTrace()
                _state.value = Response.Error(e.message.toString())
            }
        }
    }

}