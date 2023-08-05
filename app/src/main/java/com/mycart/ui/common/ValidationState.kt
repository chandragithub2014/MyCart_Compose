package com.mycart.ui.common

import com.mycart.domain.model.User

sealed class ValidationState<out T> {
    object Loading:ValidationState<Nothing>()
    data class Success<out T>(val data: T) : ValidationState<T>()
    data class Error(val errorMessage:String):ValidationState<Nothing>()

}
