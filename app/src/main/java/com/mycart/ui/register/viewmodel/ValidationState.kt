package com.mycart.ui.register.viewmodel

import com.mycart.domain.model.User

sealed class ValidationState {
    object Loading:ValidationState()
    data class Success(val user: User) : ValidationState()

}
