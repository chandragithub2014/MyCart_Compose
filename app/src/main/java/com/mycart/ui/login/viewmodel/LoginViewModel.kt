package com.mycart.ui.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class LoginViewModel(private val myCartAuthenticationRepository: MyCartAuthenticationRepository): ViewModel() {

    val responseEvent = MutableSharedFlow<Response<Any>>()

     fun fetchLoggedInUserInfo(email:String,password:String){
         viewModelScope.launch {
             try{
                 responseEvent.emit((Response.Loading))
                 val result =    myCartAuthenticationRepository.signIn(email,password)
                 result?.let {
                      val receivedFlow = myCartAuthenticationRepository.getCurrentUser()
                      receivedFlow.collect{  user ->
                          user?.let {
                              responseEvent.emit(Response.Success(it))
                          }?:run{
                              responseEvent.emit(Response.Error("Login Failed"))
                          }
                      }
                 } ?:run {
                     responseEvent.emit(Response.Error("Login Failed"))
                 }


             }catch (e:Exception){
                 e.printStackTrace()
                 responseEvent.emit(Response.Error("${e.message}"))
             }
         }
     }
}