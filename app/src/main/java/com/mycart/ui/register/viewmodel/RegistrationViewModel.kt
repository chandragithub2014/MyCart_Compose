package com.mycart.ui.register.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycart.domain.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception

class RegistrationViewModel : ViewModel(), LifecycleObserver {

    private var _userState = mutableStateOf(User())
    val userState: State<User> = _userState

    private var _errorState = mutableStateOf(FormValidationResult())
    val errorState: State<FormValidationResult> = _errorState
    val validationEvent = MutableSharedFlow<ValidationState>()
    fun onAction(registrationEvent: RegistrationEvent) {
        when (registrationEvent) {
            is RegistrationEvent.EmailChanged -> {
                _userState.value = _userState.value.copy(
                    userEmail = registrationEvent.email
                )
            }
            is RegistrationEvent.PasswordChanged -> {
                _userState.value = _userState.value.copy(
                    userPassword = registrationEvent.passWord
                )
            }
            is RegistrationEvent.ReEnterPasswordChanged -> {
                _userState.value = _userState.value.copy(
                    confirmPassWord = registrationEvent.confirmPassword
                )
            }
            is RegistrationEvent.StoreNameChanged -> {
                _userState.value = _userState.value.copy(
                    userStore = registrationEvent.storeName
                )
            }

            is RegistrationEvent.StoreLocationChanged -> {
                _userState.value = _userState.value.copy(
                    userStoreLocation = registrationEvent.storeLoc
                )
            }

            is RegistrationEvent.MobileNumberChanged -> {
                _userState.value = _userState.value.copy(
                    userMobile = registrationEvent.mobile
                )
            }

            is RegistrationEvent.PinCodeChanged -> {
                _userState.value = _userState.value.copy(
                    userPinCode = registrationEvent.pin
                )
            }
            is RegistrationEvent.AdminChanged -> {
                _userState.value = _userState.value.copy(
                    isAdmin = registrationEvent.isAdmin
                )
            }
            else -> {
                validateUserRegistration()
            }
        }
    }

 var hasErrorWhenAdmin : Boolean = false
    var hasErrorWhenNotAdmin:Boolean = false
    private fun validateUserRegistration() {
        val isEmailValid = FormValidator.validateUserEmail(_userState.value.userEmail)
        val isPassWordValid = FormValidator.validatePassWord(_userState.value.userPassword)
        val isConfirmPassValid = FormValidator.validateConfirmationPassWord(
            _userState.value.userPassword,
            _userState.value.confirmPassWord
        )
        val isStoreNameValid = FormValidator.validateStoreName(_userState.value.userStore)
        val isStoreLocationValid =
            FormValidator.validateStoreLocation(_userState.value.userStoreLocation)
        val isMobileNumberValid = FormValidator.validateMobile(_userState.value.userMobile)
        val isPinCodeValid = FormValidator.validatePin(_userState.value.userPinCode)

        _errorState.value = _errorState.value.copy(
            emailStatus = !isEmailValid
        )
        _errorState.value = _errorState.value.copy(
            passwordStatus = !isPassWordValid
        )
        _errorState.value = _errorState.value.copy(
            confirmationPasswordStatus = !isConfirmPassValid
        )
        _errorState.value = _errorState.value.copy(
            storeNameStatus = !isStoreNameValid
        )
        _errorState.value = _errorState.value.copy(
            storeLocStatus = !isStoreLocationValid
        )
        _errorState.value = _errorState.value.copy(
            mobileStatus = !isMobileNumberValid
        )
        _errorState.value = _errorState.value.copy(
            pinCodeStatus = !isPinCodeValid
        )


        hasErrorWhenNotAdmin = !( _errorState.value.emailStatus) &&
                               !( _errorState.value.passwordStatus) &&
                               !(_errorState.value.confirmationPasswordStatus)


        if(_userState.value.isAdmin){
            hasErrorWhenAdmin =  (
               ! _errorState.value.emailStatus &&
                !_errorState.value.passwordStatus &&
                !_errorState.value.confirmationPasswordStatus &&
                !_errorState.value.storeNameStatus &&
                !_errorState.value.storeLocStatus &&
                !_errorState.value.mobileStatus &&
                !_errorState.value.pinCodeStatus
            )
        }

        viewModelScope.launch {
            if (_userState.value.isAdmin) {
                if(hasErrorWhenAdmin){
                     validationEvent.emit(ValidationState.Success(_userState.value))
                }
            }else {
                if(hasErrorWhenNotAdmin){((
                    validationEvent.emit(ValidationState.Success(_userState.value))))
                }
            }
        }
    }
}