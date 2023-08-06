package com.mycart.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.MyCartRepository
import com.mycart.ui.common.ValidationState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(private val myCartRepository: MyCartRepository): ViewModel() {

    val validationEvent = MutableSharedFlow<ValidationState<Any>>()

     fun fetchLoggedInUserInfo(email:String,password:String){
         viewModelScope.launch {
             try{
                 val  user =  myCartRepository.isValidLoggedInUser(email,password)
                 user?.let {
                     validationEvent.emit(ValidationState.Success(it))
                 }?: run{
                     validationEvent.emit(ValidationState.Error("Login Failed"))
                 }

             }catch (e:Exception){
                 e.printStackTrace()
                 validationEvent.emit(ValidationState.Error("${e.message}"))
             }
         }
     }
}