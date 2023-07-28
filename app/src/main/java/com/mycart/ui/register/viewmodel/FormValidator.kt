package com.mycart.ui.register.viewmodel

object FormValidator {

    fun validateUserEmail(email: String): Boolean {
        return email.isNotEmpty() && isEmailValid(email)
    }

    fun validatePassWord(password: String): Boolean {
        return  password.isNotEmpty() && password.length >= 6
    }

    fun validateConfirmationPassWord(
        password: String,
        confirmationPassword: String
    ): Boolean {
        return confirmationPassword.isNotEmpty() && confirmationPassword == password
    }

    fun validateStoreName(storeName: String): Boolean {
        return ( storeName.isNotEmpty())
    }

    fun validateStoreLocation(storeLoc: String): Boolean {
        return ( storeLoc.isNotEmpty())
    }

    fun validateMobile(mobile: String): Boolean {
        return ( mobile.isNotEmpty() && mobile.length == 10)
    }

    fun validatePin(pinCode: String): Boolean {
        return  pinCode.isNotEmpty() && pinCode.length == 6
    }

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")

        return emailRegex.matches(email)
    }
}

data class FormValidationResult(
    val emailStatus: Boolean = false,
    val passwordStatus: Boolean = false,
    val confirmationPasswordStatus: Boolean = false,
    val storeNameStatus: Boolean = false,
    val storeLocStatus: Boolean = false,
    val mobileStatus: Boolean = false,
    val pinCodeStatus: Boolean = false
)