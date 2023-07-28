package com.mycart.ui.register.viewmodel

sealed class RegistrationEvent  {
    data class EmailChanged(val email: String): RegistrationEvent()
    data class PasswordChanged(val passWord: String): RegistrationEvent()
    data class ReEnterPasswordChanged(val confirmPassword: String): RegistrationEvent()
    data class StoreNameChanged(val storeName: String): RegistrationEvent()
    data class StoreLocationChanged(val storeLoc: String): RegistrationEvent()
    data class MobileNumberChanged(val mobile: String): RegistrationEvent()
    data class PinCodeChanged(val pin: String): RegistrationEvent()
    data class AdminChanged(val isAdmin:Boolean) : RegistrationEvent()
    object Submit: RegistrationEvent()
}