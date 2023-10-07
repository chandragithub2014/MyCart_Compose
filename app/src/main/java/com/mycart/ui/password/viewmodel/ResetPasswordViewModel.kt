package com.mycart.ui.password.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.repository.firebase.MyCartAuthenticationRepository
import com.mycart.ui.common.Response
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.Exception


class ResetPasswordViewModel(private val myCartAuthenticationRepository: MyCartAuthenticationRepository): ViewModel() {
    val resetPasswordResponseEvent = MutableSharedFlow<Response<Any>>()

    fun resetPassword(email:String){
        viewModelScope.launch {
            try{
                resetPasswordResponseEvent.emit((Response.Loading))
                 val result =    myCartAuthenticationRepository.resetPassword(email)
                if(result){
                    resetPasswordResponseEvent.emit(Response.Success("Password reset link sent to  registered Email"))
                }else{
                    resetPasswordResponseEvent.emit(Response.Error("Password Reset failed"))
                }
            }catch (e: Exception){
                e.printStackTrace()
                resetPasswordResponseEvent.emit(Response.Error("${e.message}"))
            }
        }
    }
}